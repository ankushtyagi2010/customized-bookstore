package com.bookstore.controller;

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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/preview")
public class PreviewController {

    private final CustomizedBookService customizedBookService;
    private final BookTemplateService bookTemplateService;
    private final CharacterImageService characterImageService;
    private final CartService cartService;
    private final UserService userService;

    public PreviewController(CustomizedBookService customizedBookService,
                              BookTemplateService bookTemplateService,
                              CharacterImageService characterImageService,
                              CartService cartService,
                              UserService userService) {
        this.customizedBookService = customizedBookService;
        this.bookTemplateService = bookTemplateService;
        this.characterImageService = characterImageService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/{customizedBookId}")
    public String showPreview(@PathVariable String customizedBookId,
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

        // Build character image map for preview
        Map<String, CharacterImage> characterImageMap = new HashMap<>();
        for (CustomizedBook.CharacterCustomization cc : customizedBook.getCharacterCustomizations()) {
            if (cc.getSelectedImageId() != null) {
                characterImageService.findById(cc.getSelectedImageId())
                        .ifPresent(img -> characterImageMap.put(cc.getSlotId(), img));
            }
        }

        // Generate preview pages with customizations applied
        model.addAttribute("customizedBook", customizedBook);
        model.addAttribute("template", template);
        model.addAttribute("characterImageMap", characterImageMap);
        model.addAttribute("previewPages", generatePreviewPages(template, customizedBook, characterImageMap));

        return "books/preview";
    }

    @PostMapping("/{customizedBookId}/add-to-cart")
    public String addToCart(@PathVariable String customizedBookId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomizedBook customizedBook = customizedBookService.findById(customizedBookId)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        if (!customizedBook.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        cartService.addToCart(user.getId(), customizedBookId, 1);

        redirectAttributes.addFlashAttribute("success", "Book added to cart!");
        return "redirect:/cart";
    }

    private Map<Integer, PreviewPage> generatePreviewPages(BookTemplate template,
                                                            CustomizedBook customizedBook,
                                                            Map<String, CharacterImage> characterImageMap) {
        Map<Integer, PreviewPage> previewPages = new HashMap<>();

        for (BookTemplate.PageTemplate page : template.getPages()) {
            String content = page.getContent();

            // Replace title placeholder
            if (customizedBook.getCustomTitle() != null) {
                content = content.replace("{TITLE}", customizedBook.getCustomTitle());
            }

            // Replace character placeholders
            for (CustomizedBook.CharacterCustomization cc : customizedBook.getCharacterCustomizations()) {
                // Find the slot to get the placeholder
                for (BookTemplate.CharacterSlot slot : template.getCharacterSlots()) {
                    if (slot.getSlotId().equals(cc.getSlotId())) {
                        content = content.replace(slot.getPlaceholder(), cc.getCustomName());
                        break;
                    }
                }
            }

            // Replace dedication placeholder
            if (customizedBook.getDedication() != null) {
                content = content.replace("{DEDICATION}", customizedBook.getDedication());
            }

            // Determine image URL
            String imageUrl = page.getDefaultImageUrl();
            CustomizedBook.PageCustomization pageCustomization =
                customizedBook.getPageCustomizations().get(page.getPageNumber());
            if (pageCustomization != null && pageCustomization.getCustomImageUrl() != null) {
                imageUrl = pageCustomization.getCustomImageUrl();
            }

            previewPages.put(page.getPageNumber(), new PreviewPage(
                    page.getPageNumber(),
                    content,
                    imageUrl,
                    page.getImagePosition()
            ));
        }

        return previewPages;
    }

    public record PreviewPage(int pageNumber, String content, String imageUrl, String imagePosition) {}
}
