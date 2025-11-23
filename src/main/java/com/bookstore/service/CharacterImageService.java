package com.bookstore.service;

import com.bookstore.model.CharacterImage;
import com.bookstore.repository.CharacterImageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CharacterImageService {

    private final CharacterImageRepository characterImageRepository;

    public CharacterImageService(CharacterImageRepository characterImageRepository) {
        this.characterImageRepository = characterImageRepository;
    }

    public List<CharacterImage> findAllActive() {
        return characterImageRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    public Optional<CharacterImage> findById(String id) {
        return characterImageRepository.findById(id);
    }

    public List<CharacterImage> findByCategory(String category) {
        return characterImageRepository.findByActiveTrueAndCategory(category);
    }

    public List<CharacterImage> findByStyle(String style) {
        return characterImageRepository.findByActiveTrueAndStyle(style);
    }

    public List<CharacterImage> findByCategoryAndStyle(String category, String style) {
        return characterImageRepository.findByActiveTrueAndCategoryAndStyle(category, style);
    }

    public List<CharacterImage> findByTag(String tag) {
        return characterImageRepository.findByActiveTrueAndTagsContaining(tag);
    }

    public CharacterImage save(CharacterImage characterImage) {
        if (characterImage.getCreatedAt() == null) {
            characterImage.setCreatedAt(LocalDateTime.now());
        }
        characterImage.setUpdatedAt(LocalDateTime.now());
        return characterImageRepository.save(characterImage);
    }

    public void delete(String id) {
        characterImageRepository.deleteById(id);
    }

    public List<CharacterImage> findAll() {
        return characterImageRepository.findAll();
    }
}
