package com.bookstore.service;

import com.bookstore.dto.BookCustomizationDto;
import com.bookstore.model.BookTemplate;
import com.bookstore.model.CustomizedBook;
import com.bookstore.repository.CustomizedBookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomizedBookService {

    private final CustomizedBookRepository customizedBookRepository;
    private final BookTemplateService bookTemplateService;

    public CustomizedBookService(CustomizedBookRepository customizedBookRepository,
                                  BookTemplateService bookTemplateService) {
        this.customizedBookRepository = customizedBookRepository;
        this.bookTemplateService = bookTemplateService;
    }

    public CustomizedBook createCustomizedBook(String userId, String templateId, BookCustomizationDto dto) {
        BookTemplate template = bookTemplateService.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Book template not found"));

        CustomizedBook customizedBook = CustomizedBook.builder()
                .userId(userId)
                .bookTemplateId(templateId)
                .customTitle(dto.getCustomTitle())
                .dedication(dto.getDedication())
                .characterCustomizations(dto.getCharacterCustomizations())
                .customCoverImageUrl(dto.getCustomCoverImageUrl())
                .finalPrice(calculatePrice(template, dto))
                .status(CustomizedBook.Status.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return customizedBookRepository.save(customizedBook);
    }

    public CustomizedBook updateCustomizedBook(String id, BookCustomizationDto dto) {
        CustomizedBook customizedBook = customizedBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        BookTemplate template = bookTemplateService.findById(customizedBook.getBookTemplateId())
                .orElseThrow(() -> new RuntimeException("Book template not found"));

        customizedBook.setCustomTitle(dto.getCustomTitle());
        customizedBook.setDedication(dto.getDedication());
        customizedBook.setCharacterCustomizations(dto.getCharacterCustomizations());
        customizedBook.setCustomCoverImageUrl(dto.getCustomCoverImageUrl());
        customizedBook.setFinalPrice(calculatePrice(template, dto));
        customizedBook.setUpdatedAt(LocalDateTime.now());

        return customizedBookRepository.save(customizedBook);
    }

    public Optional<CustomizedBook> findById(String id) {
        return customizedBookRepository.findById(id);
    }

    public List<CustomizedBook> findByUserId(String userId) {
        return customizedBookRepository.findByUserId(userId);
    }

    public List<CustomizedBook> findDraftsByUserId(String userId) {
        return customizedBookRepository.findByUserIdAndStatus(userId, CustomizedBook.Status.DRAFT);
    }

    public CustomizedBook updateStatus(String id, CustomizedBook.Status status) {
        CustomizedBook customizedBook = customizedBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));
        customizedBook.setStatus(status);
        customizedBook.setUpdatedAt(LocalDateTime.now());
        return customizedBookRepository.save(customizedBook);
    }

    public void setPreviewPdf(String id, String previewPdfUrl) {
        CustomizedBook customizedBook = customizedBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));
        customizedBook.setPreviewPdfUrl(previewPdfUrl);
        customizedBook.setStatus(CustomizedBook.Status.PREVIEW_READY);
        customizedBook.setUpdatedAt(LocalDateTime.now());
        customizedBookRepository.save(customizedBook);
    }

    public void setFinalPdf(String id, String finalPdfUrl) {
        CustomizedBook customizedBook = customizedBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));
        customizedBook.setFinalPdfUrl(finalPdfUrl);
        customizedBook.setStatus(CustomizedBook.Status.COMPLETED);
        customizedBook.setUpdatedAt(LocalDateTime.now());
        customizedBookRepository.save(customizedBook);
    }

    public void delete(String id) {
        customizedBookRepository.deleteById(id);
    }

    private BigDecimal calculatePrice(BookTemplate template, BookCustomizationDto dto) {
        BigDecimal price = template.getBasePrice();

        // Add extra charges for customizations if needed
        if (dto.getCustomCoverImageUrl() != null && !dto.getCustomCoverImageUrl().isEmpty()) {
            price = price.add(new BigDecimal("2.00")); // Extra for custom cover
        }

        return price;
    }
}
