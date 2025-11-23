package com.bookstore.repository;

import com.bookstore.model.BookTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTemplateRepository extends MongoRepository<BookTemplate, String> {

    List<BookTemplate> findByActiveTrue();

    List<BookTemplate> findByActiveTrueAndFeaturedTrue();

    List<BookTemplate> findByActiveTrueAndCategory(String category);

    List<BookTemplate> findByActiveTrueAndAgeGroup(String ageGroup);

    List<BookTemplate> findByActiveTrueAndCategoryAndAgeGroup(String category, String ageGroup);
}
