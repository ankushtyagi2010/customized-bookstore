package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String orderNumber;

    private String userId;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal shippingCost;

    private BigDecimal totalAmount;

    private ShippingAddress shippingAddress;

    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    public enum OrderStatus {
        PENDING,        // Order placed, awaiting processing
        CONFIRMED,      // Order confirmed
        PROCESSING,     // Books being generated
        READY,          // Books ready for download/shipping
        SHIPPED,        // Physical copies shipped
        DELIVERED,      // Order delivered
        CANCELLED       // Order cancelled
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String customizedBookId;
        private String bookTitle;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String downloadUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddress {
        private String fullName;
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private String phone;
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.tax == null) {
            this.tax = BigDecimal.ZERO;
        }
        if (this.shippingCost == null) {
            this.shippingCost = BigDecimal.ZERO;
        }

        this.totalAmount = this.subtotal.add(this.tax).add(this.shippingCost);
    }
}
