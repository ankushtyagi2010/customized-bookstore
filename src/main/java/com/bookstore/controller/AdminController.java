package com.bookstore.controller;

import com.bookstore.model.BookTemplate;
import com.bookstore.model.CharacterImage;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookTemplateService bookTemplateService;
    private final CharacterImageService characterImageService;
    private final OrderService orderService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public AdminController(BookTemplateService bookTemplateService,
                           CharacterImageService characterImageService,
                           OrderService orderService,
                           UserService userService,
                           FileStorageService fileStorageService) {
        this.bookTemplateService = bookTemplateService;
        this.characterImageService = characterImageService;
        this.orderService = orderService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalBooks", bookTemplateService.findAll().size());
        model.addAttribute("totalOrders", orderService.findAll().size());
        model.addAttribute("totalUsers", userService.findAllUsers().size());
        model.addAttribute("pendingOrders", orderService.findByStatus(Order.OrderStatus.PENDING).size());
        return "admin/dashboard";
    }

    // Book Template Management
    @GetMapping("/books")
    public String listBooks(Model model) {
        List<BookTemplate> books = bookTemplateService.findAll();
        model.addAttribute("books", books);
        return "admin/books/list";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new BookTemplate());
        return "admin/books/form";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable String id, Model model) {
        BookTemplate book = bookTemplateService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        return "admin/books/form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute BookTemplate book,
                           @RequestParam(required = false) MultipartFile coverImage,
                           RedirectAttributes redirectAttributes) {
        try {
            if (coverImage != null && !coverImage.isEmpty()) {
                String imageUrl = fileStorageService.storeCoverImage(coverImage);
                book.setCoverImageUrl(imageUrl);
            }
            bookTemplateService.save(book);
            redirectAttributes.addFlashAttribute("success", "Book saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable String id, RedirectAttributes redirectAttributes) {
        bookTemplateService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/toggle-active/{id}")
    public String toggleBookActive(@PathVariable String id, RedirectAttributes redirectAttributes) {
        bookTemplateService.toggleActive(id);
        redirectAttributes.addFlashAttribute("success", "Book status updated!");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/toggle-featured/{id}")
    public String toggleBookFeatured(@PathVariable String id, RedirectAttributes redirectAttributes) {
        bookTemplateService.toggleFeatured(id);
        redirectAttributes.addFlashAttribute("success", "Book featured status updated!");
        return "redirect:/admin/books";
    }

    // Character Image Management
    @GetMapping("/characters")
    public String listCharacters(Model model) {
        List<CharacterImage> characters = characterImageService.findAll();
        model.addAttribute("characters", characters);
        return "admin/characters/list";
    }

    @GetMapping("/characters/new")
    public String newCharacterForm(Model model) {
        model.addAttribute("character", new CharacterImage());
        return "admin/characters/form";
    }

    @GetMapping("/characters/edit/{id}")
    public String editCharacterForm(@PathVariable String id, Model model) {
        CharacterImage character = characterImageService.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found"));
        model.addAttribute("character", character);
        return "admin/characters/form";
    }

    @PostMapping("/characters/save")
    public String saveCharacter(@ModelAttribute CharacterImage character,
                                 @RequestParam(required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileStorageService.storeCharacterImage(imageFile);
                character.setImageUrl(imageUrl);
                character.setThumbnailUrl(imageUrl); // Could generate separate thumbnail
            }
            characterImageService.save(character);
            redirectAttributes.addFlashAttribute("success", "Character saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving character: " + e.getMessage());
        }
        return "redirect:/admin/characters";
    }

    @PostMapping("/characters/delete/{id}")
    public String deleteCharacter(@PathVariable String id, RedirectAttributes redirectAttributes) {
        characterImageService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Character deleted successfully!");
        return "redirect:/admin/characters";
    }

    // Order Management
    @GetMapping("/orders")
    public String listOrders(@RequestParam(required = false) String status, Model model) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.findByStatus(Order.OrderStatus.valueOf(status));
        } else {
            orders = orderService.findAll();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", Order.OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin/orders/list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable String id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/orders/details";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable String id,
                                     @RequestParam Order.OrderStatus status,
                                     RedirectAttributes redirectAttributes) {
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Order status updated!");
        return "redirect:/admin/orders/" + id;
    }

    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }
}
