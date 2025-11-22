# CustomBooks - Personalized Book Store

A web application for creating and ordering customized books where customers can personalize book titles, character names, choose animated character images, and customize book covers.

## Technology Stack

- **Backend:** Java 17, Spring Boot 3.2
- **Frontend:** HTML5, CSS3, Vanilla JavaScript, Thymeleaf
- **Database:** MongoDB
- **PDF Generation:** iText 7
- **Image Processing:** Thumbnailator

## Features

### Customer Features
- Browse book catalog with filtering by category and age group
- Customize books with:
  - Custom book titles
  - Personalized character names
  - Animated character selection from gallery
  - Custom cover images (upload or select)
  - Personal dedication messages
- Live preview of customized books
- Shopping cart management
- Order placement and tracking
- Download PDF versions of purchased books

### Admin Features
- Dashboard with statistics
- Manage book templates (CRUD operations)
- Manage character images
- Order management and status updates
- User management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB 4.4+ (running on localhost:27017)

## Installation & Setup

1. **Clone the repository:**
   ```bash
   cd /home/ankus/bookstore
   ```

2. **Start MongoDB:**
   ```bash
   # Make sure MongoDB is running on localhost:27017
   mongod
   ```

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application:**
   - Open browser and navigate to: http://localhost:8080

## Default Users

The application creates sample users on first run:

| Role     | Email                    | Password     |
|----------|--------------------------|--------------|
| Admin    | admin@custombooks.com    | admin123     |
| Customer | customer@example.com     | customer123  |

## Project Structure

```
bookstore/
├── src/main/java/com/bookstore/
│   ├── config/          # Configuration classes
│   ├── controller/      # MVC Controllers
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # Entity models
│   ├── repository/      # MongoDB repositories
│   ├── service/         # Business logic
│   └── util/            # Utility classes
├── src/main/resources/
│   ├── static/          # CSS, JS, images
│   ├── templates/       # Thymeleaf templates
│   └── application.yml  # Configuration
└── pom.xml
```

## Key Endpoints

| Endpoint                | Description                    |
|-------------------------|--------------------------------|
| `/`                     | Home page                      |
| `/books`                | Book catalog                   |
| `/books/{id}`           | Book details                   |
| `/customize/{id}`       | Customization wizard           |
| `/preview/{id}`         | Preview customized book        |
| `/cart`                 | Shopping cart                  |
| `/orders`               | Order history                  |
| `/orders/checkout`      | Checkout page                  |
| `/admin`                | Admin dashboard                |
| `/admin/books`          | Manage book templates          |
| `/admin/characters`     | Manage character images        |
| `/admin/orders`         | Manage orders                  |

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/bookstore

app:
  upload:
    dir: ${user.home}/bookstore-uploads
```

## File Storage

Uploaded files are stored in:
- Character images: `~/bookstore-uploads/characters/`
- Cover images: `~/bookstore-uploads/covers/`
- User uploads: `~/bookstore-uploads/user-uploads/`
- Generated PDFs: `~/bookstore-uploads/pdfs/`

## Adding Sample Images

To add sample images for the demo:

1. Create placeholder images in `src/main/resources/static/images/`:
   - `books/adventure-cover.jpg`
   - `books/birthday-cover.jpg`
   - `books/bedtime-cover.jpg`
   - `characters/boy1.png`
   - `characters/girl1.png`
   - `characters/dragon.png`
   - `characters/owl.png`
   - `characters/puppy.png`

2. Or upload images through the admin panel after starting the application.

## Development

### Running in Development Mode

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Building for Production

```bash
mvn clean package -DskipTests
java -jar target/customized-bookstore-1.0.0.jar
```

## Future Enhancements

- [ ] Payment integration (Stripe/PayPal)
- [ ] Email notifications
- [ ] Print-on-demand integration
- [ ] Social sharing
- [ ] Wishlist feature
- [ ] Reviews and ratings
- [ ] Gift options
- [ ] Multiple language support

## License

MIT License
