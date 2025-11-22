package com.bookstore.dto;

import com.bookstore.model.CustomizedBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCustomizationDto {

    private String bookTemplateId;

    private String customTitle;

    private String dedication;

    @Builder.Default
    private List<CustomizedBook.CharacterCustomization> characterCustomizations = new ArrayList<>();

    private String customCoverImageUrl;
}
