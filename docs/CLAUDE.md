# Claude Code Project Guide

This document provides context and guidelines for Claude Code when working with the CustomBooks project.

## Project Overview

CustomBooks is a Spring Boot web application for selling personalized/customized books online. Customers can:
- Browse book templates
- Customize books with custom titles, characters, and dedications
- Preview customized books
- Add to cart and checkout
- Track orders

## Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.2 |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Template Engine | Thymeleaf |
| Database | MongoDB |
| PDF Generation | iText 7 |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |

## Project Structure

```
customized-bookstore/
├── src/
│   └── main/
│       ├── java/com/bookstore/
│       │   ├── config/           # Spring configuration
│       │   ├── controller/       # MVC controllers (8 controllers)
│       │   ├── dto/              # Data transfer objects (5 DTOs)
│       │   ├── model/            # MongoDB entities (6 models)
│       │   ├── repository/       # Spring Data repositories
│       │   ├── service/          # Business logic (9 services)
│       │   └── BookstoreApplication.java
│       └── resources/
│           ├── static/
│           │   ├── css/style.css # Main stylesheet (~2000 lines, dark theme)
│           │   ├── js/main.js    # JavaScript utilities
│           │   └── images/       # Static images
│           ├── templates/
│           │   ├── fragments/    # Reusable header/footer
│           │   ├── admin/        # Admin panel pages
│           │   ├── auth/         # Login/register
│           │   ├── book/         # Catalog, details, customize
│           │   ├── order/        # Cart, checkout, orders
│           │   ├── profile/      # User profile (NEW)
│           │   └── *.html        # Public pages
│           └── application.yml   # Configuration
├── docs/                         # Documentation
├── Dockerfile                    # Multi-stage Docker build
├── docker-compose.yml            # Container orchestration
└── pom.xml                       # Maven dependencies
```

## Key Files to Know

### Configuration
- `src/main/resources/application.yml` - Main app configuration
- `src/main/java/com/bookstore/config/SecurityConfig.java` - Spring Security setup
- `src/main/java/com/bookstore/config/DataInitializer.java` - Sample data seeding

### Styling
- `src/main/resources/static/css/style.css` - Dark theme CSS with CSS variables

### Controllers
- `HomeController.java` - Public pages (home, about, contact)
- `AuthController.java` - Login/registration
- `BookController.java` - Book catalog and details
- `CustomizeController.java` - Book customization wizard
- `CartController.java` - Shopping cart
- `OrderController.java` - Checkout, orders, order cancellation
- `ProfileController.java` - User profile, addresses, password change
- `AdminController.java` - Admin panel operations

### Services
- `UserService.java` - User management, addresses, password change
- `BookTemplateService.java` - Book template CRUD
- `CustomizedBookService.java` - Book customization logic
- `CartService.java` - Cart operations
- `OrderService.java` - Order processing, cancellation, filtering
- `PdfGenerationService.java` - PDF book generation

### DTOs (Data Transfer Objects)
- `UserRegistrationDto.java` - Registration form validation
- `BookCustomizationDto.java` - Book customization form
- `ProfileUpdateDto.java` - Profile edit form
- `ChangePasswordDto.java` - Password change form
- `AddressDto.java` - Address form validation

## UI Theme

The application uses a **dark theme** with the following CSS variables:

```css
--primary-color: #818cf8;        /* Indigo accent */
--bg-primary: #0f172a;           /* Slate 900 - main background */
--bg-secondary: #1e293b;         /* Slate 800 - cards, navbar */
--text-primary: #f1f5f9;         /* Slate 100 - main text */
--text-secondary: #94a3b8;       /* Slate 400 - secondary text */
```

### Interactive Elements
- Form inputs have hover lift effects (`transform: translateY(-1px)`)
- Focus states show glowing borders with primary color
- Smooth transitions using `cubic-bezier(0.4, 0, 0.2, 1)`

## Common Tasks

### Running Locally

```bash
# With Maven
mvn spring-boot:run

# With Docker
docker-compose up -d --build
```

### After Making CSS/UI Changes

```bash
docker-compose down
docker-compose build --no-cache app
docker-compose up -d
```

### Accessing the Application

| URL | Purpose |
|-----|---------|
| http://localhost:8080 | Main application |
| http://localhost:8081 | MongoDB Express (with admin profile) |

### Default Credentials

| Email | Password | Role |
|-------|----------|------|
| admin@custombooks.com | admin123 | ADMIN |
| customer@example.com | customer123 | CUSTOMER |

## Development Guidelines

### Adding New Features

1. Create/update MongoDB model in `model/` package
2. Create repository interface in `repository/` package
3. Implement business logic in `service/` package
4. Create controller in `controller/` package
5. Add Thymeleaf template in `templates/` folder
6. Update CSS in `style.css` if needed

### CSS Conventions

- Use CSS variables for colors and shadows
- Follow existing dark theme patterns
- Use BEM-style naming for new classes
- Add hover/focus states for interactive elements
- Use `transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1)` for animations

### Template Conventions

- Use Thymeleaf fragments for header/footer
- Include CSRF tokens in forms
- Use `th:href` and `th:src` for URLs
- Follow responsive design patterns

## Database Collections

| Collection | Purpose |
|------------|---------|
| users | User accounts and profiles |
| book_templates | Book story templates |
| customized_books | User's customized books |
| character_images | Character image gallery |
| cart_items | Shopping cart |
| orders | Order records |

## Testing

```bash
# Run tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Build without tests
mvn package -DskipTests
```

## Troubleshooting

### MongoDB Connection Issues
```bash
# Check if MongoDB is running
docker-compose ps
docker-compose logs mongodb
```

### Application Not Starting
```bash
# Check app logs
docker-compose logs app

# Verify health check
curl http://localhost:8080/actuator/health
```

### CSS Changes Not Reflecting
```bash
# Force rebuild without cache
docker-compose build --no-cache app
docker-compose up -d
```

## Feature Overview

### User Profile (`/profile`)
- **Personal Info Tab**: Edit first name, last name, phone number
- **Addresses Tab**: Manage shipping addresses (add, delete, set default)
- **Security Tab**: Change password, view account status
- Profile header with avatar, stats (total orders, books created)

### Order Management (`/orders`)
- **Order List**: Filter by status, search by order number
- **Order Details**: View complete order information with timeline
- **Order Cancellation**: Cancel PENDING/CONFIRMED orders
- **Print Invoice**: Print-friendly order details
- **Order Confirmation**: Success page after checkout with next steps

### Template Structure
```
templates/
├── profile/
│   └── profile.html          # User profile page (tabbed interface)
├── order/
│   ├── list.html             # Orders with filters
│   ├── details.html          # Order details with cancel/print
│   ├── confirmation.html     # Post-checkout success page
│   └── checkout.html         # Checkout form
└── ...
```

## Related Documentation

- [API.md](./API.md) - API endpoint documentation
- [ARCHITECTURE.md](./ARCHITECTURE.md) - System architecture
- [SETUP.md](./SETUP.md) - Detailed setup guide
- [CONTRIBUTING.md](./CONTRIBUTING.md) - Contribution guidelines
- [CONVERSATION_CONTEXT.md](./CONVERSATION_CONTEXT.md) - Development history

---

*Last updated: November 23, 2025 - Phase 7 (Profile & Order Enhancements)*
