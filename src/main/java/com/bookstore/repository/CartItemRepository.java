package com.bookstore.repository;

import com.bookstore.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {

    List<CartItem> findByUserId(String userId);

    Optional<CartItem> findByUserIdAndCustomizedBookId(String userId, String customizedBookId);

    void deleteByUserId(String userId);

    long countByUserId(String userId);
}
