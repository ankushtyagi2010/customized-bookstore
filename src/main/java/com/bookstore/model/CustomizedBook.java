package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customized_books")
public class CustomizedBook {

    @Id
    private String id;

    private String userId;

    private String bookTemplateId;

    private String customTitle;

    private String dedication; // Personal dedication message

    // Maps character slot IDs to customization
    @Builder.Default
    private List<CharacterCustomization> characterCustomizations = new ArrayList<>();

    // Custom cover image (user uploaded or selected)
    private String customCoverImageUrl;

    // Page-specific customizations
    @Builder.Default
    private Map<Integer, PageCustomization> pageCustomizations = new HashMap<>();

    private BigDecimal finalPrice;

    private String previewPdfUrl;

    private String finalPdfUrl;

    @Builder.Default
    private Status status = Status.DRAFT;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum Status {
        DRAFT,          // Still being customized
        PREVIEW_READY,  // Preview generated
        ORDERED,        // Added to an order
        PROCESSING,     // PDF being generated
        COMPLETED       // Final PDF ready
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterCustomization {
        private String slotId;
        private String customName;
        private String selectedImageId;
        private String customImageUrl; // If user uploads their own
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageCustomization {
        private int pageNumber;
        private String customImageUrl;
        private String additionalText;
    }
}
