package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.service.CartService;
import com.bookstore.service.OrderService;
import com.bookstore.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String listOrders(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String search,
                             Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders;

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orders = orderService.findByUserIdAndStatus(user.getId(), orderStatus);
            } catch (IllegalArgumentException e) {
                orders = orderService.findByUserId(user.getId());
            }
        } else {
            orders = orderService.findByUserId(user.getId());
        }

        // Filter by search term (order number) if provided
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = search.trim().toLowerCase();
            orders = orders.stream()
                    .filter(o -> o.getOrderNumber().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("searchTerm", search);
        model.addAttribute("statuses", Order.OrderStatus.values());

        return "order/list";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@PathVariable String orderId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        model.addAttribute("order", order);
        return "order/details";
    }

    @GetMapping("/checkout")
    public String showCheckout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cartService.getCartItemCount(user.getId()) == 0) {
            return "redirect:/cart";
        }

        model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));
        model.addAttribute("addresses", user.getAddresses());
        model.addAttribute("shippingAddress", new Order.ShippingAddress());

        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute Order.ShippingAddress shippingAddress,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Order order = orderService.createOrderFromCart(user.getId(), shippingAddress);
            return "redirect:/orders/confirmation/" + order.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable String orderId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        model.addAttribute("order", order);
        return "order/confirmation";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable String orderId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            orderService.cancelOrder(orderId, user.getId());
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/orders/" + orderId;
    }
}
