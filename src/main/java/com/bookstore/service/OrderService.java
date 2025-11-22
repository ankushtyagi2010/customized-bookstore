package com.bookstore.service;

import com.bookstore.model.CartItem;
import com.bookstore.model.CustomizedBook;
import com.bookstore.model.Order;
import com.bookstore.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CustomizedBookService customizedBookService;

    public OrderService(OrderRepository orderRepository, CartService cartService,
                        CustomizedBookService customizedBookService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.customizedBookService = customizedBookService;
    }

    public Order createOrderFromCart(String userId, Order.ShippingAddress shippingAddress) {
        List<CartItem> cartItems = cartService.getCartItems(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<Order.OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            CustomizedBook book = customizedBookService.findById(cartItem.getCustomizedBookId())
                    .orElseThrow(() -> new RuntimeException("Customized book not found"));

            Order.OrderItem orderItem = Order.OrderItem.builder()
                    .customizedBookId(cartItem.getCustomizedBookId())
                    .bookTitle(book.getCustomTitle())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();

            orderItems.add(orderItem);

            // Update customized book status
            customizedBookService.updateStatus(book.getId(), CustomizedBook.Status.ORDERED);
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(userId)
                .items(orderItems)
                .shippingAddress(shippingAddress)
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        order.calculateTotals();

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after order creation
        cartService.clearCart(userId);

        return savedOrder;
    }

    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public List<Order> findByUserId(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order updateStatus(String orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == Order.OrderStatus.DELIVERED) {
            order.setCompletedAt(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    public Order setItemDownloadUrl(String orderId, String customizedBookId, String downloadUrl) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.getItems().stream()
                .filter(item -> item.getCustomizedBookId().equals(customizedBookId))
                .findFirst()
                .ifPresent(item -> item.setDownloadUrl(downloadUrl));

        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }
}
