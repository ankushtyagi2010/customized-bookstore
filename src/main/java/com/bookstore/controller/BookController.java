package com.bookstore.controller;

import com.bookstore.model.BookTemplate;
import com.bookstore.model.CharacterImage;
import com.bookstore.service.BookTemplateService;
import com.bookstore.service.CharacterImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookTemplateService bookTemplateService;
    private final CharacterImageService characterImageService;

    public BookController(BookTemplateService bookTemplateService,
                          CharacterImageService characterImageService) {
        this.bookTemplateService = bookTemplateService;
        this.characterImageService = characterImageService;
    }

    @GetMapping
    public String listBooks(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String ageGroup,
                            Model model) {

        List<BookTemplate> books;

        if (category != null && ageGroup != null) {
            books = bookTemplateService.findByCategoryAndAgeGroup(category, ageGroup);
        } else if (category != null) {
            books = bookTemplateService.findByCategory(category);
        } else if (ageGroup != null) {
            books = bookTemplateService.findByAgeGroup(ageGroup);
        } else {
            books = bookTemplateService.findAllActive();
        }

        model.addAttribute("books", books);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedAgeGroup", ageGroup);

        return "books/catalog";
    }

    @GetMapping("/{id}")
    public String bookDetails(@PathVariable String id, Model model) {
        BookTemplate book = bookTemplateService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<CharacterImage> characterImages = characterImageService.findAllActive();

        model.addAttribute("book", book);
        model.addAttribute("characterImages", characterImages);

        return "books/details";
    }
}
