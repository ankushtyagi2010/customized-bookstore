package com.bookstore.repository;

import com.bookstore.model.CharacterImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterImageRepository extends MongoRepository<CharacterImage, String> {

    List<CharacterImage> findByActiveTrueOrderBySortOrderAsc();

    List<CharacterImage> findByActiveTrueAndCategory(String category);

    List<CharacterImage> findByActiveTrueAndStyle(String style);

    List<CharacterImage> findByActiveTrueAndCategoryAndStyle(String category, String style);

    List<CharacterImage> findByActiveTrueAndTagsContaining(String tag);
}
