# Architecture Documentation

## System Overview

CustomBooks is a web application built using a layered architecture pattern with Spring Boot. The application follows the MVC (Model-View-Controller) pattern for web layer organization.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              Browser (HTML/CSS/JavaScript)               │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  Controllers │  │   Thymeleaf  │  │   Static Resources   │  │
│  │              │  │   Templates  │  │   (CSS/JS/Images)    │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                               │
│  ┌────────────────┐  ┌────────────────┐  ┌─────────────────┐   │
│  │  UserService   │  │ BookTemplate   │  │ CustomizedBook  │   │
│  │                │  │    Service     │  │    Service      │   │
│  └────────────────┘  └────────────────┘  └─────────────────┘   │
│  ┌────────────────┐  ┌────────────────┐  ┌─────────────────┐   │
│  │  CartService   │  │  OrderService  │  │ PdfGeneration   │   │
│  │                │  │                │  │    Service      │   │
│  └────────────────┘  └────────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                              │
│  ┌────────────────┐  ┌────────────────┐  ┌─────────────────┐   │
│  │ UserRepository │  │ BookTemplate   │  │ CustomizedBook  │   │
│  │                │  │  Repository    │  │   Repository    │   │
│  └────────────────┘  └────────────────┘  └─────────────────┘   │
│  ┌────────────────┐  ┌────────────────┐  ┌─────────────────┐   │
│  │ CartItem       │  │ Order          │  │ CharacterImage  │   │
│  │ Repository     │  │ Repository     │  │   Repository    │   │
│  └────────────────┘  └────────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA LAYER                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                      MongoDB                             │    │
│  │   Collections: users, book_templates, customized_books,  │    │
│  │                orders, cart_items, character_images      │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## Layer Descriptions

### 1. Client Layer
- **Technology:** HTML5, CSS3, Vanilla JavaScript
- **Responsibility:** User interface rendering and client-side interactions
- **Components:**
  - Thymeleaf-rendered HTML pages
  - Responsive CSS styling
  - JavaScript for dynamic interactions

### 2. Presentation Layer
- **Technology:** Spring MVC, Thymeleaf
- **Responsibility:** Handle HTTP requests, render views, manage user sessions
- **Components:**
  - Controllers (REST endpoints)
  - Thymeleaf templates
  - Static resources

### 3. Service Layer
- **Technology:** Spring Services
- **Responsibility:** Business logic implementation
- **Components:**
  - User management
  - Book customization logic
  - Order processing
  - PDF generation
  - File storage

### 4. Repository Layer
- **Technology:** Spring Data MongoDB
- **Responsibility:** Data access and persistence
- **Components:**
  - MongoDB repositories
  - Custom queries

### 5. Data Layer
- **Technology:** MongoDB
- **Responsibility:** Data storage and retrieval
- **Collections:**
  - users
  - book_templates
  - customized_books
  - character_images
  - cart_items
  - orders

## Data Flow

### Book Customization Flow
```
1. User browses catalog → BookController
2. User selects book → BookTemplateService
3. User customizes → CustomizeController
4. Save customization → CustomizedBookService
5. Generate preview → PreviewController
6. Add to cart → CartService
7. Checkout → OrderService
8. Generate PDF → PdfGenerationService
```

### Authentication Flow
```
1. User registers/logs in → AuthController
2. Validate credentials → CustomUserDetailsService
3. Create session → Spring Security
4. Store user context → SecurityContext
```

## Security Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Security Filter Chain                     │
├─────────────────────────────────────────────────────────────┤
│  1. Authentication Filter                                    │
│  2. Authorization Filter                                     │
│  3. CSRF Protection                                          │
│  4. Session Management                                       │
└─────────────────────────────────────────────────────────────┘

Access Control:
├── Public: /, /home, /books, /about, /contact, /login, /register
├── Authenticated: /cart, /checkout, /orders, /customize, /preview
└── Admin Only: /admin/**
```

## File Storage Architecture

```
~/bookstore-uploads/
├── characters/      # Character images uploaded by admin
├── covers/          # Book cover images
├── user-uploads/    # User-uploaded custom images
└── pdfs/           # Generated PDF books
```

## Database Schema

### Users Collection
```json
{
  "_id": "ObjectId",
  "email": "string (unique)",
  "password": "string (bcrypt)",
  "firstName": "string",
  "lastName": "string",
  "role": "CUSTOMER | ADMIN",
  "addresses": [Address],
  "enabled": "boolean",
  "createdAt": "datetime"
}
```

### Book Templates Collection
```json
{
  "_id": "ObjectId",
  "title": "string",
  "description": "string",
  "category": "string",
  "ageGroup": "string",
  "basePrice": "decimal",
  "coverImageUrl": "string",
  "pages": [PageTemplate],
  "characterSlots": [CharacterSlot],
  "customizationOptions": {
    "allowTitleCustomization": "boolean",
    "allowCoverCustomization": "boolean",
    "allowDedication": "boolean"
  },
  "active": "boolean",
  "featured": "boolean"
}
```

### Customized Books Collection
```json
{
  "_id": "ObjectId",
  "userId": "string",
  "bookTemplateId": "string",
  "customTitle": "string",
  "dedication": "string",
  "characterCustomizations": [CharacterCustomization],
  "customCoverImageUrl": "string",
  "finalPrice": "decimal",
  "status": "DRAFT | PREVIEW_READY | ORDERED | COMPLETED",
  "previewPdfUrl": "string",
  "finalPdfUrl": "string"
}
```

### Orders Collection
```json
{
  "_id": "ObjectId",
  "orderNumber": "string",
  "userId": "string",
  "items": [OrderItem],
  "subtotal": "decimal",
  "tax": "decimal",
  "shippingCost": "decimal",
  "totalAmount": "decimal",
  "shippingAddress": "ShippingAddress",
  "status": "PENDING | CONFIRMED | PROCESSING | READY | DELIVERED"
}
```

## Technology Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Backend Framework | Spring Boot 3.2 | Mature, enterprise-ready, excellent MongoDB support |
| Database | MongoDB | Flexible schema for varying book structures |
| Template Engine | Thymeleaf | Server-side rendering, Spring integration |
| PDF Library | iText 7 | Industry standard, full-featured |
| Image Processing | Thumbnailator | Simple API, good performance |
| Authentication | Spring Security | Comprehensive security framework |
| Build Tool | Maven | Widely adopted, good dependency management |
