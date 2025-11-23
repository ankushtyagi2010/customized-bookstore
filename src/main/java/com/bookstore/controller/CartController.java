package com.bookstore.controller;

import com.bookstore.model.CartItem;
import com.bookstore.model.CustomizedBook;
import com.bookstore.model.User;
import com.bookstore.service.CartService;
import com.bookstore.service.CustomizedBookService;
import com.bookstore.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CustomizedBookService customizedBookService;
    private final UserService userService;

    public CartController(CartService cartService,
                          CustomizedBookService customizedBookService,
                          UserService userService) {
        this.cartService = cartService;
        this.customizedBookService = customizedBookService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        List<CartItemView> cartItemViews = new ArrayList<>();

        for (CartItem item : cartItems) {
            CustomizedBook book = customizedBookService.findById(item.getCustomizedBookId())
                    .orElse(null);
            if (book != null) {
                cartItemViews.add(new CartItemView(item, book));
            }
        }

        BigDecimal total = cartService.getCartTotal(user.getId());

        model.addAttribute("cartItems", cartItemViews);
        model.addAttribute("total", total);

        return "order/cart";
    }

    @PostMapping("/add/{customizedBookId}")
    public String addToCart(@PathVariable String customizedBookId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.addToCart(user.getId(), customizedBookId, quantity);

        redirectAttributes.addFlashAttribute("success", "Item added to cart!");
        return "redirect:/cart";
    }

    @PostMapping("/update/{cartItemId}")
    public String updateQuantity(@PathVariable String cartItemId,
                                  @RequestParam int quantity,
                                  RedirectAttributes redirectAttributes) {

        cartService.updateQuantity(cartItemId, quantity);

        redirectAttributes.addFlashAttribute("success", "Cart updated!");
        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeFromCart(@PathVariable String cartItemId,
                                  RedirectAttributes redirectAttributes) {

        cartService.removeFromCart(cartItemId);

        redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.clearCart(user.getId());

        redirectAttributes.addFlashAttribute("success", "Cart cleared!");
        return "redirect:/cart";
    }

    public record CartItemView(CartItem cartItem, CustomizedBook customizedBook) {}
}
