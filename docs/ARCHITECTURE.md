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
- **Theme:** Dark Theme with interactive elements
- **Components:**
  - Thymeleaf-rendered HTML pages
  - Responsive CSS styling with CSS custom properties (variables)
  - JavaScript for dynamic interactions
  - Interactive form controls with hover/focus effects
  - Shadow effects and glow animations

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

### Profile Management Flow
```
1. User accesses profile → ProfileController
2. View/edit personal info → UserService.updateProfile()
3. Manage addresses → UserService.addAddress() / deleteAddress()
4. Change password → UserService.changePassword()
5. Display stats → OrderService / CustomizedBookService
```

### Order Cancellation Flow
```
1. User requests cancellation → OrderController.cancelOrder()
2. Validate ownership → OrderService.cancelOrder()
3. Check status (PENDING/CONFIRMED only)
4. Update status to CANCELLED
5. Revert customized books → CustomizedBookService.updateStatus(PREVIEW_READY)
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
├── Authenticated: /cart, /checkout, /orders, /customize, /preview, /profile
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
  "status": "PENDING | CONFIRMED | PROCESSING | READY | DELIVERED | CANCELLED"
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
| Development Environment | VS Code Devcontainer | Consistent environment, zero-config setup |
| Containerization | Docker & Docker Compose | Simplified deployment and development |

## Development Environment Architecture

### VS Code Devcontainer

The project includes a complete devcontainer setup for development:

```
.devcontainer/
├── devcontainer.json              # VS Code configuration
├── Dockerfile                     # Development container image
├── docker-compose.devcontainer.yml # Development services
└── README.md                      # Documentation
```

**Container Architecture:**
```
┌─────────────────────────────────────────────────────────────┐
│                   Devcontainer Services                      │
├─────────────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────────┐     │
│  │  app-dev (Development Container)                   │     │
│  │  ├─ Java 17 JDK                                    │     │
│  │  ├─ Maven 3.9.6                                    │     │
│  │  ├─ VS Code Server with Extensions                │     │
│  │  ├─ Git with credential forwarding                │     │
│  │  └─ Spring DevTools (Hot Reload)                  │     │
│  └────────────────────────────────────────────────────┘     │
│                          │                                   │
│  ┌────────────────────────────────────────────────────┐     │
│  │  mongodb (Database Service)                        │     │
│  │  ├─ MongoDB 7.0                                    │     │
│  │  ├─ Persistent volume                             │     │
│  │  └─ Health checks                                 │     │
│  └────────────────────────────────────────────────────┘     │
│                          │                                   │
│  ┌────────────────────────────────────────────────────┐     │
│  │  mongo-express (Optional Admin UI)                 │     │
│  │  └─ Web interface on port 8081                    │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

**Key Features:**
- **Workspace Mounting:** Project files mounted from host to `/workspace`
- **Git Integration:** `.gitconfig` and `.ssh` mounted for seamless git operations
- **Volume Persistence:** Maven cache (`~/.m2`) persisted across rebuilds
- **Port Forwarding:** 8080 (app), 27017 (MongoDB), 8081 (Mongo Express), 5005 (debug)
- **VS Code Extensions:** Java, Spring Boot, Lombok, MongoDB, GitLens pre-installed
- **Auto-build:** `mvn clean install -DskipTests` runs on container creation

## UI/Theme Architecture

### Dark Theme Design

The application uses a modern dark theme implemented with CSS custom properties (variables) for consistent styling across all components.

#### CSS Variables Structure

```css
:root {
    /* Primary Colors */
    --primary-color: #818cf8;        /* Indigo accent */
    --primary-hover: #6366f1;        /* Darker indigo */
    --primary-glow: rgba(129, 140, 248, 0.4);

    /* Background Colors */
    --bg-primary: #0f172a;           /* Slate 900 - darkest */
    --bg-secondary: #1e293b;         /* Slate 800 */
    --bg-tertiary: #334155;          /* Slate 700 */
    --bg-card: #1e293b;              /* Card backgrounds */
    --bg-input: #1e293b;             /* Input backgrounds */

    /* Text Colors */
    --text-primary: #f1f5f9;         /* Slate 100 */
    --text-secondary: #94a3b8;       /* Slate 400 */

    /* Shadow Effects */
    --input-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.3);
    --input-shadow-focus: 0 0 0 3px var(--primary-glow);
    --glow-shadow: 0 0 20px rgba(129, 140, 248, 0.3);
}
```

### Interactive Form Controls

All form inputs feature:
- **Hover Effects:** Subtle lift with `transform: translateY(-1px)`
- **Focus Effects:** Glowing border with primary color
- **Shadow Effects:** Layered box shadows for depth
- **Smooth Transitions:** Cubic-bezier easing for natural motion

```css
.form-control:hover {
    transform: translateY(-1px);
    box-shadow: var(--input-shadow), 0 0 0 1px rgba(129, 140, 248, 0.1);
}

.form-control:focus {
    border-color: var(--primary-color);
    box-shadow: var(--input-shadow-focus);
    transform: translateY(-2px);
}
```

### Component Styling

| Component | Background | Border | Shadow |
|-----------|------------|--------|--------|
| Cards | `--bg-card` | `--border-color` | Layered shadow |
| Inputs | `--bg-input` | `--border-color` | `--input-shadow` |
| Buttons | `--primary-color` | None | Glow on hover |
| Navbar | `--bg-secondary` | Bottom border | Subtle shadow |
| Footer | `--bg-secondary` | Top border | None |

---

*Last updated: November 23, 2025 - Phase 7 (Profile & Order Enhancements)*
