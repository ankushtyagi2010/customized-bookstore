package com.bookstore.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.character-images}")
    private String characterImagesDir;

    @Value("${app.upload.cover-images}")
    private String coverImagesDir;

    @Value("${app.upload.user-uploads}")
    private String userUploadsDir;

    @Value("${app.upload.generated-pdfs}")
    private String generatedPdfsDir;

    public String storeCharacterImage(MultipartFile file) throws IOException {
        return storeFile(file, characterImagesDir, "character");
    }

    public String storeCoverImage(MultipartFile file) throws IOException {
        return storeFile(file, coverImagesDir, "cover");
    }

    public String storeUserUpload(MultipartFile file) throws IOException {
        return storeFile(file, userUploadsDir, "user");
    }

    public String storeGeneratedPdf(byte[] pdfContent, String filename) throws IOException {
        Path directory = Paths.get(generatedPdfsDir);
        Files.createDirectories(directory);

        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        Path filePath = directory.resolve(uniqueFilename);

        Files.write(filePath, pdfContent);

        return "/uploads/pdfs/" + uniqueFilename;
    }

    private String storeFile(MultipartFile file, String directory, String prefix) throws IOException {
        Path uploadPath = Paths.get(directory);
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";

        String uniqueFilename = prefix + "_" + UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative URL for serving
        String relativePath = directory.substring(directory.lastIndexOf("/") + 1);
        return "/uploads/" + relativePath + "/" + uniqueFilename;
    }

    public String createThumbnail(String originalImagePath, int width, int height) throws IOException {
        Path original = Paths.get(originalImagePath);
        String filename = original.getFileName().toString();
        String thumbnailFilename = "thumb_" + filename;
        Path thumbnailPath = original.getParent().resolve(thumbnailFilename);

        Thumbnails.of(original.toFile())
                .size(width, height)
                .toFile(thumbnailPath.toFile());

        return thumbnailPath.toString();
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }

    public byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
}
