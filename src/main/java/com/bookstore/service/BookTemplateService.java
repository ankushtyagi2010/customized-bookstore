package com.bookstore.service;

import com.bookstore.model.BookTemplate;
import com.bookstore.repository.BookTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookTemplateService {

    private final BookTemplateRepository bookTemplateRepository;

    public BookTemplateService(BookTemplateRepository bookTemplateRepository) {
        this.bookTemplateRepository = bookTemplateRepository;
    }

    public List<BookTemplate> findAllActive() {
        return bookTemplateRepository.findByActiveTrue();
    }

    public List<BookTemplate> findFeatured() {
        return bookTemplateRepository.findByActiveTrueAndFeaturedTrue();
    }

    public Optional<BookTemplate> findById(String id) {
        return bookTemplateRepository.findById(id);
    }

    public List<BookTemplate> findByCategory(String category) {
        return bookTemplateRepository.findByActiveTrueAndCategory(category);
    }

    public List<BookTemplate> findByAgeGroup(String ageGroup) {
        return bookTemplateRepository.findByActiveTrueAndAgeGroup(ageGroup);
    }

    public List<BookTemplate> findByCategoryAndAgeGroup(String category, String ageGroup) {
        return bookTemplateRepository.findByActiveTrueAndCategoryAndAgeGroup(category, ageGroup);
    }

    public BookTemplate save(BookTemplate bookTemplate) {
        if (bookTemplate.getCreatedAt() == null) {
            bookTemplate.setCreatedAt(LocalDateTime.now());
        }
        bookTemplate.setUpdatedAt(LocalDateTime.now());
        bookTemplate.setPageCount(bookTemplate.getPages().size());
        return bookTemplateRepository.save(bookTemplate);
    }

    public void delete(String id) {
        bookTemplateRepository.deleteById(id);
    }

    public BookTemplate toggleActive(String id) {
        BookTemplate template = bookTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book template not found"));
        template.setActive(!template.isActive());
        template.setUpdatedAt(LocalDateTime.now());
        return bookTemplateRepository.save(template);
    }

    public BookTemplate toggleFeatured(String id) {
        BookTemplate template = bookTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book template not found"));
        template.setFeatured(!template.isFeatured());
        template.setUpdatedAt(LocalDateTime.now());
        return bookTemplateRepository.save(template);
    }

    public List<BookTemplate> findAll() {
        return bookTemplateRepository.findAll();
    }
}
