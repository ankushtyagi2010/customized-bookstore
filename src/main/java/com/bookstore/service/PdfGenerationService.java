package com.bookstore.service;

import com.bookstore.model.BookTemplate;
import com.bookstore.model.CharacterImage;
import com.bookstore.model.CustomizedBook;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfGenerationService {

    private final BookTemplateService bookTemplateService;
    private final CharacterImageService characterImageService;
    private final CustomizedBookService customizedBookService;
    private final FileStorageService fileStorageService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public PdfGenerationService(BookTemplateService bookTemplateService,
                                 CharacterImageService characterImageService,
                                 CustomizedBookService customizedBookService,
                                 FileStorageService fileStorageService) {
        this.bookTemplateService = bookTemplateService;
        this.characterImageService = characterImageService;
        this.customizedBookService = customizedBookService;
        this.fileStorageService = fileStorageService;
    }

    public String generatePreviewPdf(String customizedBookId) throws IOException {
        CustomizedBook customizedBook = customizedBookService.findById(customizedBookId)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        BookTemplate template = bookTemplateService.findById(customizedBook.getBookTemplateId())
                .orElseThrow(() -> new RuntimeException("Book template not found"));

        byte[] pdfContent = generatePdfContent(customizedBook, template, true);

        String filename = "preview_" + customizedBookId + ".pdf";
        String pdfUrl = fileStorageService.storeGeneratedPdf(pdfContent, filename);

        customizedBookService.setPreviewPdf(customizedBookId, pdfUrl);

        return pdfUrl;
    }

    public String generateFinalPdf(String customizedBookId) throws IOException {
        CustomizedBook customizedBook = customizedBookService.findById(customizedBookId)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        BookTemplate template = bookTemplateService.findById(customizedBook.getBookTemplateId())
                .orElseThrow(() -> new RuntimeException("Book template not found"));

        byte[] pdfContent = generatePdfContent(customizedBook, template, false);

        String filename = "book_" + customizedBookId + ".pdf";
        String pdfUrl = fileStorageService.storeGeneratedPdf(pdfContent, filename);

        customizedBookService.setFinalPdf(customizedBookId, pdfUrl);

        return pdfUrl;
    }

    private byte[] generatePdfContent(CustomizedBook customizedBook,
                                       BookTemplate template,
                                       boolean isPreview) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A5);

        try {
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Build character name replacements
            Map<String, String> replacements = buildReplacements(customizedBook, template);

            // Cover Page
            addCoverPage(document, customizedBook, template, titleFont);

            // Dedication Page (if present)
            if (customizedBook.getDedication() != null && !customizedBook.getDedication().isEmpty()) {
                document.add(new Paragraph("\n\n\n"));
                document.add(new Paragraph("Dedication")
                        .setFont(titleFont)
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph(customizedBook.getDedication())
                        .setFont(bodyFont)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setItalic());
                pdfDoc.addNewPage();
            }

            // Content Pages
            for (BookTemplate.PageTemplate page : template.getPages()) {
                addContentPage(document, pdfDoc, page, customizedBook, replacements, bodyFont, titleFont, isPreview);
            }

            // Watermark for preview
            if (isPreview) {
                // Add preview watermark to all pages
                addPreviewWatermark(document);
            }

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    private void addCoverPage(Document document,
                               CustomizedBook customizedBook,
                               BookTemplate template,
                               PdfFont titleFont) throws IOException {

        // Title
        String title = customizedBook.getCustomTitle() != null ?
                customizedBook.getCustomTitle() : template.getTitle();

        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph(title)
                .setFont(titleFont)
                .setFontSize(28)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY));

        // Cover Image
        String coverImagePath = customizedBook.getCustomCoverImageUrl() != null ?
                customizedBook.getCustomCoverImageUrl() : template.getCoverImageUrl();

        if (coverImagePath != null) {
            try {
                Path imagePath = resolveImagePath(coverImagePath);
                if (Files.exists(imagePath)) {
                    Image coverImage = new Image(ImageDataFactory.create(imagePath.toString()));
                    coverImage.setWidth(300);
                    coverImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    document.add(new Paragraph("\n"));
                    document.add(coverImage);
                }
            } catch (Exception e) {
                // Skip image if it can't be loaded
            }
        }

        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("A Personalized Story")
                .setFont(titleFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));
    }

    private void addContentPage(Document document,
                                 PdfDocument pdfDoc,
                                 BookTemplate.PageTemplate page,
                                 CustomizedBook customizedBook,
                                 Map<String, String> replacements,
                                 PdfFont bodyFont,
                                 PdfFont titleFont,
                                 boolean isPreview) throws IOException {

        pdfDoc.addNewPage();

        String content = page.getContent();

        // Apply all replacements
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }

        // Determine image for this page
        String imageUrl = page.getDefaultImageUrl();
        CustomizedBook.PageCustomization pageCustomization =
                customizedBook.getPageCustomizations().get(page.getPageNumber());
        if (pageCustomization != null && pageCustomization.getCustomImageUrl() != null) {
            imageUrl = pageCustomization.getCustomImageUrl();
        }

        // Layout based on image position
        String imagePosition = page.getImagePosition() != null ? page.getImagePosition() : "TOP";

        switch (imagePosition) {
            case "TOP":
                addImage(document, imageUrl);
                document.add(new Paragraph(content).setFont(bodyFont).setFontSize(12));
                break;
            case "BOTTOM":
                document.add(new Paragraph(content).setFont(bodyFont).setFontSize(12));
                addImage(document, imageUrl);
                break;
            case "FULL":
                addImage(document, imageUrl, 400);
                break;
            default:
                document.add(new Paragraph(content).setFont(bodyFont).setFontSize(12));
                addImage(document, imageUrl);
        }

        // Page number
        document.add(new Paragraph(String.valueOf(page.getPageNumber()))
                .setFont(bodyFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));
    }

    private void addImage(Document document, String imageUrl) throws IOException {
        addImage(document, imageUrl, 250);
    }

    private void addImage(Document document, String imageUrl, float width) throws IOException {
        if (imageUrl == null) return;

        try {
            Path imagePath = resolveImagePath(imageUrl);
            if (Files.exists(imagePath)) {
                Image image = new Image(ImageDataFactory.create(imagePath.toString()));
                image.setWidth(width);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(image);
            }
        } catch (Exception e) {
            // Skip image if it can't be loaded
        }
    }

    private Map<String, String> buildReplacements(CustomizedBook customizedBook, BookTemplate template) {
        Map<String, String> replacements = new HashMap<>();

        // Title replacement
        if (customizedBook.getCustomTitle() != null) {
            replacements.put("{TITLE}", customizedBook.getCustomTitle());
        }

        // Dedication replacement
        if (customizedBook.getDedication() != null) {
            replacements.put("{DEDICATION}", customizedBook.getDedication());
        }

        // Character replacements
        for (CustomizedBook.CharacterCustomization cc : customizedBook.getCharacterCustomizations()) {
            for (BookTemplate.CharacterSlot slot : template.getCharacterSlots()) {
                if (slot.getSlotId().equals(cc.getSlotId())) {
                    replacements.put(slot.getPlaceholder(), cc.getCustomName());
                    break;
                }
            }
        }

        return replacements;
    }

    private Path resolveImagePath(String imageUrl) {
        // Handle both absolute paths and relative URLs
        if (imageUrl.startsWith("/uploads/")) {
            String relativePath = imageUrl.substring("/uploads/".length());
            return Paths.get(uploadDir, relativePath);
        }
        return Paths.get(imageUrl);
    }

    private void addPreviewWatermark(Document document) {
        document.add(new Paragraph("\n\n--- PREVIEW ---")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.LIGHT_GRAY));
    }
}
