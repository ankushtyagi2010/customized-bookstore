package com.bookstore.controller;

import com.bookstore.dto.BookCustomizationDto;
import com.bookstore.model.BookTemplate;
import com.bookstore.model.CharacterImage;
import com.bookstore.model.CustomizedBook;
import com.bookstore.model.User;
import com.bookstore.service.BookTemplateService;
import com.bookstore.service.CartService;
import com.bookstore.service.CharacterImageService;
import com.bookstore.service.CustomizedBookService;
import com.bookstore.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customize")
public class CustomizeController {

    private final BookTemplateService bookTemplateService;
    private final CharacterImageService characterImageService;
    private final CustomizedBookService customizedBookService;
    private final CartService cartService;
    private final UserService userService;

    public CustomizeController(BookTemplateService bookTemplateService,
                                CharacterImageService characterImageService,
                                CustomizedBookService customizedBookService,
                                CartService cartService,
                                UserService userService) {
        this.bookTemplateService = bookTemplateService;
        this.characterImageService = characterImageService;
        this.customizedBookService = customizedBookService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/{templateId}")
    public String showCustomizationWizard(@PathVariable String templateId, Model model) {
        BookTemplate template = bookTemplateService.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Book template not found"));

        List<CharacterImage> characterImages = characterImageService.findAllActive();

        model.addAttribute("template", template);
        model.addAttribute("characterImages", characterImages);
        model.addAttribute("customization", new BookCustomizationDto());

        return "books/customize";
    }

    @PostMapping("/{templateId}")
    public String saveCustomization(@PathVariable String templateId,
                                     @ModelAttribute BookCustomizationDto customizationDto,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        customizationDto.setBookTemplateId(templateId);

        CustomizedBook customizedBook = customizedBookService.createCustomizedBook(
                user.getId(), templateId, customizationDto);

        return "redirect:/preview/" + customizedBook.getId();
    }

    @GetMapping("/edit/{customizedBookId}")
    public String editCustomization(@PathVariable String customizedBookId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomizedBook customizedBook = customizedBookService.findById(customizedBookId)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        if (!customizedBook.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        BookTemplate template = bookTemplateService.findById(customizedBook.getBookTemplateId())
                .orElseThrow(() -> new RuntimeException("Book template not found"));

        List<CharacterImage> characterImages = characterImageService.findAllActive();

        BookCustomizationDto dto = BookCustomizationDto.builder()
                .bookTemplateId(customizedBook.getBookTemplateId())
                .customTitle(customizedBook.getCustomTitle())
                .dedication(customizedBook.getDedication())
                .characterCustomizations(customizedBook.getCharacterCustomizations())
                .customCoverImageUrl(customizedBook.getCustomCoverImageUrl())
                .build();

        model.addAttribute("template", template);
        model.addAttribute("characterImages", characterImages);
        model.addAttribute("customization", dto);
        model.addAttribute("customizedBookId", customizedBookId);

        return "books/customize";
    }

    @PostMapping("/edit/{customizedBookId}")
    public String updateCustomization(@PathVariable String customizedBookId,
                                       @ModelAttribute BookCustomizationDto customizationDto,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomizedBook existingBook = customizedBookService.findById(customizedBookId)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        if (!existingBook.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        customizedBookService.updateCustomizedBook(customizedBookId, customizationDto);

        return "redirect:/preview/" + customizedBookId;
    }
}
