package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    @Builder.Default
    private Role role = Role.CUSTOMER;

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum Role {
        CUSTOMER, ADMIN
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String id;
        private String label;
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private boolean isDefault;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
