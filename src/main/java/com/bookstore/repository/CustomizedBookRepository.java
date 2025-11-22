package com.bookstore.repository;

import com.bookstore.model.CustomizedBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomizedBookRepository extends MongoRepository<CustomizedBook, String> {

    List<CustomizedBook> findByUserId(String userId);

    List<CustomizedBook> findByUserIdAndStatus(String userId, CustomizedBook.Status status);

    List<CustomizedBook> findByBookTemplateId(String bookTemplateId);
}
