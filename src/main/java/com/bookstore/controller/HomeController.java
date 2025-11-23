package com.bookstore.controller;

import com.bookstore.model.BookTemplate;
import com.bookstore.service.BookTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BookTemplateService bookTemplateService;

    public HomeController(BookTemplateService bookTemplateService) {
        this.bookTemplateService = bookTemplateService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<BookTemplate> featuredBooks = bookTemplateService.findFeatured();
        model.addAttribute("featuredBooks", featuredBooks);
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
