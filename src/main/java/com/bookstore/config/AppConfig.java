package com.bookstore.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AppConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.character-images}")
    private String characterImagesDir;

    @Value("${app.upload.cover-images}")
    private String coverImagesDir;

    @Value("${app.upload.user-uploads}")
    private String userUploadsDir;

    @Value("${app.upload.generated-pdfs}")
    private String generatedPdfsDir;

    @PostConstruct
    public void init() throws IOException {
        createDirectoryIfNotExists(uploadDir);
        createDirectoryIfNotExists(characterImagesDir);
        createDirectoryIfNotExists(coverImagesDir);
        createDirectoryIfNotExists(userUploadsDir);
        createDirectoryIfNotExists(generatedPdfsDir);
    }

    private void createDirectoryIfNotExists(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
