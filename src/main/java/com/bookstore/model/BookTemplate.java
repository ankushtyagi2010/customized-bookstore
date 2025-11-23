package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "book_templates")
public class BookTemplate {

    @Id
    private String id;

    private String title;

    private String description;

    private String category;

    private BigDecimal basePrice;

    private String coverImageUrl;

    private String thumbnailUrl;

    // The story template with placeholders like {CHARACTER_NAME}, {TITLE}
    @Builder.Default
    private List<PageTemplate> pages = new ArrayList<>();

    // Default characters that can be customized
    @Builder.Default
    private List<CharacterSlot> characterSlots = new ArrayList<>();

    // Customization options
    @Builder.Default
    private CustomizationOptions customizationOptions = new CustomizationOptions();

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean featured = false;

    private String ageGroup; // e.g., "3-5", "6-8", "9-12"

    private int pageCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageTemplate {
        private int pageNumber;
        private String content; // Text with placeholders
        private String defaultImageUrl;
        private String imagePosition; // TOP, BOTTOM, LEFT, RIGHT, FULL
        private boolean allowImageCustomization;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterSlot {
        private String slotId;
        private String slotName; // e.g., "Main Character", "Best Friend"
        private String placeholder; // e.g., "{MAIN_CHARACTER}"
        private String defaultName;
        private String defaultImageId;
        private boolean required;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationOptions {
        @Builder.Default
        private boolean allowTitleCustomization = true;
        @Builder.Default
        private boolean allowCoverCustomization = true;
        @Builder.Default
        private boolean allowDedication = true;
        @Builder.Default
        private int maxCharacters = 3;
    }
}
