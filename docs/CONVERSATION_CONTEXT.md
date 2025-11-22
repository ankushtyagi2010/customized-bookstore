# Conversation Context - CustomBooks Project Development

This document captures the full context of the conversation that led to the creation of this project.

## Project Inception

**Date:** November 22, 2025

**User Request:**
> "Plan a web project which can work as an online book store. The book store specifically sells customized books. Customers can order a book with custom titles and custom characters, can also choose animated character photos, and book photos can be customized."

## Requirements Gathering

### Initial Clarification Questions Asked:

1. **Java Framework Preference**
   - Options: Spring Boot, Jakarta EE, Micronaut
   - **Selected:** Spring Boot

2. **Frontend Technology**
   - Options: Vanilla JavaScript, jQuery, Thymeleaf templates
   - **Selected:** Vanilla JavaScript (with Thymeleaf for server-side rendering)

3. **Database Choice**
   - Options: MySQL, PostgreSQL, MongoDB
   - **Selected:** MongoDB

4. **Payment Integration**
   - Options: Stripe, PayPal, Both
   - **Selected:** Leave for later (not implemented in this version)

### Technology Stack Decided:
- **Backend:** Java 17 with Spring Boot 3.2
- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Template Engine:** Thymeleaf
- **Database:** MongoDB
- **PDF Generation:** iText 7
- **Image Processing:** Thumbnailator

## Development Plan

The following implementation plan was created and approved:

### Phase 1: Foundation
1. Initialize Spring Boot project with Maven and dependencies
2. Create MongoDB configuration and connection setup
3. Create domain models (User, BookTemplate, CustomizedBook, Order, etc.)

### Phase 2: Authentication & Core
4. Build user authentication with Spring Security
5. Create repositories and services for all entities
6. Build book catalog controllers and pages

### Phase 3: Customization Features
7. Implement book customization wizard
8. Build live preview functionality

### Phase 4: E-Commerce
9. Implement shopping cart
10. Create order management system

### Phase 5: Administration
11. Build admin panel
12. Implement PDF generation for customized books

## Implementation Details

### Files Created

#### Java Classes (27 files)

**Main Application:**
- `BookstoreApplication.java` - Spring Boot entry point

**Models (6 files):**
- `User.java` - User entity with roles (CUSTOMER/ADMIN)
- `BookTemplate.java` - Book template with pages, character slots, customization options
- `CustomizedBook.java` - User's customized version of a book
- `CharacterImage.java` - Animated character images for selection
- `Order.java` - Order with items, shipping, status tracking
- `CartItem.java` - Shopping cart items

**Repositories (6 files):**
- `UserRepository.java`
- `BookTemplateRepository.java`
- `CustomizedBookRepository.java`
- `CharacterImageRepository.java`
- `OrderRepository.java`
- `CartItemRepository.java`

**Services (9 files):**
- `UserService.java` - User registration, profile management
- `CustomUserDetailsService.java` - Spring Security integration
- `BookTemplateService.java` - Book template CRUD operations
- `CharacterImageService.java` - Character image management
- `CustomizedBookService.java` - Book customization logic
- `CartService.java` - Shopping cart operations
- `OrderService.java` - Order processing
- `FileStorageService.java` - File upload handling
- `PdfGenerationService.java` - PDF book generation with iText

**Controllers (8 files):**
- `HomeController.java` - Home, about, contact pages
- `AuthController.java` - Login, registration
- `BookController.java` - Book catalog, details
- `CustomizeController.java` - Customization wizard
- `PreviewController.java` - Book preview
- `CartController.java` - Shopping cart
- `OrderController.java` - Orders and checkout
- `AdminController.java` - Admin panel operations

**Configuration (5 files):**
- `SecurityConfig.java` - Spring Security configuration
- `MongoConfig.java` - MongoDB settings
- `AppConfig.java` - Application initialization
- `WebConfig.java` - Static resource handling
- `DataInitializer.java` - Sample data seeding

**DTOs (2 files):**
- `UserRegistrationDto.java`
- `BookCustomizationDto.java`

#### HTML Templates (24 files)

**Public Pages:**
- `home.html` - Landing page with featured books
- `about.html` - About us page
- `contact.html` - Contact form and FAQ
- `access-denied.html` - 403 error page

**Authentication:**
- `user/login.html` - Login form
- `user/register.html` - Registration form

**Book Pages:**
- `books/catalog.html` - Book listing with filters
- `books/details.html` - Book detail view
- `books/customize.html` - Multi-step customization wizard
- `books/preview.html` - Book preview with page navigation

**Order Pages:**
- `order/cart.html` - Shopping cart
- `order/checkout.html` - Checkout with address form
- `order/list.html` - Order history
- `order/details.html` - Order details with timeline

**Admin Pages:**
- `admin/dashboard.html` - Statistics and quick actions
- `admin/books/list.html` - Book template management
- `admin/books/form.html` - Book template editor
- `admin/characters/list.html` - Character image gallery
- `admin/characters/form.html` - Character image upload
- `admin/orders/list.html` - Order management
- `admin/orders/details.html` - Order details with status update
- `admin/users/list.html` - User listing

**Fragments:**
- `fragments/header.html` - Navigation bar
- `fragments/footer.html` - Footer

#### Static Resources
- `css/style.css` - Complete responsive stylesheet (800+ lines)
- `js/main.js` - JavaScript utilities and interactions

#### Configuration Files
- `pom.xml` - Maven dependencies
- `application.yml` - Spring Boot configuration
- `.gitignore` - Git ignore rules

## Key Features Implemented

### 1. User Authentication
- Registration with validation
- Login with Spring Security
- Role-based access control (Customer/Admin)
- Session management

### 2. Book Catalog
- List all active books
- Filter by category and age group
- Featured books on home page
- Detailed book view with customization options

### 3. Book Customization Wizard
- 4-step wizard interface
- Custom title input
- Personal dedication message
- Character name customization
- Character image selection from gallery
- Cover image selection/upload
- Real-time review before saving

### 4. Live Preview
- Page-by-page book preview
- Keyboard navigation support
- All customizations applied
- Add to cart from preview

### 5. Shopping Cart
- Add/remove items
- Update quantities
- Price calculation
- Persistent cart per user

### 6. Order Management
- Checkout with shipping address
- Order confirmation
- Order history
- Status tracking with timeline
- PDF download (when ready)

### 7. Admin Panel
- Dashboard with statistics
- Book template CRUD
- Character image management
- Order status updates
- User listing

### 8. PDF Generation
- Cover page with custom title and image
- Dedication page
- Story pages with replaced placeholders
- Character images embedded
- Preview watermark support

## Database Design

### Collections Created:
1. **users** - User accounts and profiles
2. **book_templates** - Story templates with pages and character slots
3. **customized_books** - User customizations
4. **character_images** - Animated character gallery
5. **cart_items** - Shopping cart
6. **orders** - Order records

## Sample Data

The application seeds the following on first run:

### Users:
- Admin: admin@custombooks.com / admin123
- Customer: customer@example.com / customer123

### Character Images:
- Happy Boy (cartoon, boy)
- Adventure Girl (cartoon, girl)
- Friendly Dragon (cartoon, fantasy)
- Wise Owl (cartoon, animal)
- Playful Puppy (cartoon, animal)

### Book Templates:
1. **The Great Adventure** - Adventure story for ages 3-5 ($24.99)
2. **The Birthday Surprise** - Birthday story for ages 3-5 ($19.99)
3. **Sweet Dreams Journey** - Bedtime story for ages 0-2 ($21.99)

## Future Enhancements Discussed

- Payment integration (Stripe/PayPal)
- Email notifications
- Print-on-demand integration
- Social sharing
- Wishlist feature
- Reviews and ratings
- Gift options
- Multiple language support

## Session Summary

This project was developed in a single session with the following approach:

1. **Planning Phase:** Gathered requirements, clarified technology choices
2. **Foundation Phase:** Set up project structure, models, and repositories
3. **Core Features Phase:** Implemented services and controllers
4. **UI Phase:** Created all HTML templates with responsive CSS
5. **Enhancement Phase:** Added sample data initializer and documentation

The entire codebase was written from scratch with clean architecture principles, following Spring Boot best practices.

---

*This document was generated as part of the project development conversation on November 22, 2025.*
