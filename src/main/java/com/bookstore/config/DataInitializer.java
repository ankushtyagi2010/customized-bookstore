package com.bookstore.config;

import com.bookstore.model.BookTemplate;
import com.bookstore.model.CharacterImage;
import com.bookstore.model.User;
import com.bookstore.repository.BookTemplateRepository;
import com.bookstore.repository.CharacterImageRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")
    CommandLineRunner initDatabase(UserRepository userRepository,
                                    BookTemplateRepository bookTemplateRepository,
                                    CharacterImageRepository characterImageRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // Only initialize if database is empty
            if (userRepository.count() == 0) {
                initUsers(userRepository, passwordEncoder);
            }

            if (characterImageRepository.count() == 0) {
                initCharacterImages(characterImageRepository);
            }

            if (bookTemplateRepository.count() == 0) {
                initBookTemplates(bookTemplateRepository);
            }
        };
    }

    private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // Create admin user
        User admin = User.builder()
                .email("admin@custombooks.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role(User.Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);

        // Create test customer
        User customer = User.builder()
                .email("customer@example.com")
                .password(passwordEncoder.encode("customer123"))
                .firstName("John")
                .lastName("Doe")
                .phone("555-123-4567")
                .role(User.Role.CUSTOMER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(customer);

        System.out.println("=== Sample Users Created ===");
        System.out.println("Admin: admin@custombooks.com / admin123");
        System.out.println("Customer: customer@example.com / customer123");
    }

    private void initCharacterImages(CharacterImageRepository repository) {
        List<CharacterImage> characters = Arrays.asList(
                CharacterImage.builder()
                        .name("Happy Boy")
                        .description("A cheerful young boy with brown hair")
                        .category("boy")
                        .style("cartoon")
                        .imageUrl("/images/characters/boy1.png")
                        .thumbnailUrl("/images/characters/boy1.png")
                        .tags(Arrays.asList("boy", "happy", "brown-hair"))
                        .active(true)
                        .sortOrder(1)
                        .createdAt(LocalDateTime.now())
                        .build(),
                CharacterImage.builder()
                        .name("Adventure Girl")
                        .description("A brave girl with blonde hair ready for adventure")
                        .category("girl")
                        .style("cartoon")
                        .imageUrl("/images/characters/girl1.png")
                        .thumbnailUrl("/images/characters/girl1.png")
                        .tags(Arrays.asList("girl", "brave", "blonde"))
                        .active(true)
                        .sortOrder(2)
                        .createdAt(LocalDateTime.now())
                        .build(),
                CharacterImage.builder()
                        .name("Friendly Dragon")
                        .description("A cute and friendly baby dragon")
                        .category("fantasy")
                        .style("cartoon")
                        .imageUrl("/images/characters/dragon.png")
                        .thumbnailUrl("/images/characters/dragon.png")
                        .tags(Arrays.asList("dragon", "fantasy", "cute"))
                        .active(true)
                        .sortOrder(3)
                        .createdAt(LocalDateTime.now())
                        .build(),
                CharacterImage.builder()
                        .name("Wise Owl")
                        .description("A wise owl companion")
                        .category("animal")
                        .style("cartoon")
                        .imageUrl("/images/characters/owl.png")
                        .thumbnailUrl("/images/characters/owl.png")
                        .tags(Arrays.asList("owl", "animal", "wise"))
                        .active(true)
                        .sortOrder(4)
                        .createdAt(LocalDateTime.now())
                        .build(),
                CharacterImage.builder()
                        .name("Playful Puppy")
                        .description("An adorable playful puppy")
                        .category("animal")
                        .style("cartoon")
                        .imageUrl("/images/characters/puppy.png")
                        .thumbnailUrl("/images/characters/puppy.png")
                        .tags(Arrays.asList("puppy", "dog", "cute"))
                        .active(true)
                        .sortOrder(5)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        repository.saveAll(characters);
        System.out.println("=== Sample Character Images Created ===");
    }

    private void initBookTemplates(BookTemplateRepository repository) {
        // Adventure Book
        BookTemplate adventureBook = BookTemplate.builder()
                .title("The Great Adventure")
                .description("Join the hero on an exciting journey through magical lands filled with wonder and discovery.")
                .category("adventure")
                .ageGroup("3-5")
                .basePrice(new BigDecimal("24.99"))
                .coverImageUrl("/images/books/adventure-cover.jpg")
                .thumbnailUrl("/images/books/adventure-thumb.jpg")
                .active(true)
                .featured(true)
                .customizationOptions(BookTemplate.CustomizationOptions.builder()
                        .allowTitleCustomization(true)
                        .allowCoverCustomization(true)
                        .allowDedication(true)
                        .maxCharacters(2)
                        .build())
                .characterSlots(Arrays.asList(
                        BookTemplate.CharacterSlot.builder()
                                .slotId(UUID.randomUUID().toString())
                                .slotName("Main Hero")
                                .placeholder("{HERO_NAME}")
                                .defaultName("Alex")
                                .required(true)
                                .build(),
                        BookTemplate.CharacterSlot.builder()
                                .slotId(UUID.randomUUID().toString())
                                .slotName("Best Friend")
                                .placeholder("{FRIEND_NAME}")
                                .defaultName("Sam")
                                .required(false)
                                .build()
                ))
                .pages(Arrays.asList(
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(1)
                                .content("Once upon a time, there was a brave child named {HERO_NAME} who dreamed of going on the greatest adventure ever.")
                                .imagePosition("TOP")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(2)
                                .content("{HERO_NAME} packed a backpack with snacks and a compass, ready to explore the mysterious forest beyond the garden.")
                                .imagePosition("BOTTOM")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(3)
                                .content("Along the way, {HERO_NAME} met {FRIEND_NAME}, who became the best adventure companion anyone could ask for!")
                                .imagePosition("TOP")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(4)
                                .content("Together, {HERO_NAME} and {FRIEND_NAME} discovered a hidden treasure and learned that the best adventures are the ones shared with friends.")
                                .imagePosition("FULL")
                                .allowImageCustomization(true)
                                .build()
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Birthday Book
        BookTemplate birthdayBook = BookTemplate.builder()
                .title("The Birthday Surprise")
                .description("A magical birthday story where the child is the star of their special day!")
                .category("birthday")
                .ageGroup("3-5")
                .basePrice(new BigDecimal("19.99"))
                .coverImageUrl("/images/books/birthday-cover.jpg")
                .thumbnailUrl("/images/books/birthday-thumb.jpg")
                .active(true)
                .featured(true)
                .customizationOptions(BookTemplate.CustomizationOptions.builder()
                        .allowTitleCustomization(true)
                        .allowCoverCustomization(true)
                        .allowDedication(true)
                        .maxCharacters(1)
                        .build())
                .characterSlots(Arrays.asList(
                        BookTemplate.CharacterSlot.builder()
                                .slotId(UUID.randomUUID().toString())
                                .slotName("Birthday Child")
                                .placeholder("{CHILD_NAME}")
                                .defaultName("Emma")
                                .required(true)
                                .build()
                ))
                .pages(Arrays.asList(
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(1)
                                .content("Today is a very special day! It's {CHILD_NAME}'s birthday!")
                                .imagePosition("TOP")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(2)
                                .content("{CHILD_NAME} woke up to balloons and streamers everywhere. What a wonderful surprise!")
                                .imagePosition("TOP")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(3)
                                .content("All of {CHILD_NAME}'s friends came to celebrate with games, cake, and lots of laughter.")
                                .imagePosition("BOTTOM")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(4)
                                .content("Happy Birthday, {CHILD_NAME}! May all your wishes come true!")
                                .imagePosition("FULL")
                                .allowImageCustomization(true)
                                .build()
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Bedtime Book
        BookTemplate bedtimeBook = BookTemplate.builder()
                .title("Sweet Dreams Journey")
                .description("A calming bedtime story to help little ones drift off to dreamland.")
                .category("bedtime")
                .ageGroup("0-2")
                .basePrice(new BigDecimal("21.99"))
                .coverImageUrl("/images/books/bedtime-cover.jpg")
                .thumbnailUrl("/images/books/bedtime-thumb.jpg")
                .active(true)
                .featured(false)
                .customizationOptions(BookTemplate.CustomizationOptions.builder()
                        .allowTitleCustomization(true)
                        .allowCoverCustomization(true)
                        .allowDedication(true)
                        .maxCharacters(1)
                        .build())
                .characterSlots(Arrays.asList(
                        BookTemplate.CharacterSlot.builder()
                                .slotId(UUID.randomUUID().toString())
                                .slotName("Sleepy Child")
                                .placeholder("{SLEEPY_NAME}")
                                .defaultName("Riley")
                                .required(true)
                                .build()
                ))
                .pages(Arrays.asList(
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(1)
                                .content("The stars are twinkling in the sky, and it's time for {SLEEPY_NAME} to say goodnight.")
                                .imagePosition("TOP")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(2)
                                .content("{SLEEPY_NAME} snuggles under the warm blanket, feeling cozy and safe.")
                                .imagePosition("TOP")
                                .allowImageCustomization(true)
                                .build(),
                        BookTemplate.PageTemplate.builder()
                                .pageNumber(3)
                                .content("Sweet dreams, {SLEEPY_NAME}. Tomorrow brings new adventures!")
                                .imagePosition("FULL")
                                .allowImageCustomization(true)
                                .build()
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        repository.saveAll(Arrays.asList(adventureBook, birthdayBook, bedtimeBook));
        System.out.println("=== Sample Book Templates Created ===");
    }
}
