package com.bookstore.service;

import com.bookstore.model.CartItem;
import com.bookstore.model.CustomizedBook;
import com.bookstore.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CustomizedBookService customizedBookService;

    public CartService(CartItemRepository cartItemRepository, CustomizedBookService customizedBookService) {
        this.cartItemRepository = cartItemRepository;
        this.customizedBookService = customizedBookService;
    }

    public List<CartItem> getCartItems(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(String userId, String customizedBookId, int quantity) {
        CustomizedBook book = customizedBookService.findById(customizedBookId)
                .orElseThrow(() -> new RuntimeException("Customized book not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndCustomizedBookId(userId, customizedBookId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setUpdatedAt(LocalDateTime.now());
            return cartItemRepository.save(item);
        }

        CartItem cartItem = CartItem.builder()
                .userId(userId)
                .customizedBookId(customizedBookId)
                .quantity(quantity)
                .unitPrice(book.getFinalPrice())
                .addedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return cartItemRepository.save(cartItem);
    }

    public CartItem updateQuantity(String cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(String cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public long getCartItemCount(String userId) {
        return cartItemRepository.countByUserId(userId);
    }

    public BigDecimal getCartTotal(String userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
