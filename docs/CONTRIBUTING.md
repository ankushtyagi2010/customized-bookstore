# Contributing to CustomBooks

Thank you for your interest in contributing to CustomBooks! This document provides guidelines and instructions for contributing.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](../../issues)
2. If not, create a new issue with:
   - Clear, descriptive title
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots if applicable
   - Environment details (OS, Java version, browser)

### Suggesting Features

1. Check existing [Issues](../../issues) for similar suggestions
2. Create a new issue with:
   - Clear description of the feature
   - Use case / problem it solves
   - Proposed implementation (optional)

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Write/update tests if applicable
5. Ensure code compiles: `mvn clean compile`
6. Commit with meaningful messages
7. Push to your fork
8. Create a Pull Request

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- MongoDB 4.4+
- Git

### Local Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/customized-bookstore.git
cd customized-bookstore

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL_OWNER/customized-bookstore.git

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and small
- Add JavaDoc for public methods
- Maximum line length: 120 characters

### Example:
```java
/**
 * Finds a book template by its unique identifier.
 *
 * @param id the book template ID
 * @return Optional containing the book template if found
 */
public Optional<BookTemplate> findById(String id) {
    return bookTemplateRepository.findById(id);
}
```

### HTML/CSS Standards

- Use semantic HTML5 elements
- Follow BEM naming convention for CSS classes
- Mobile-first responsive design
- Maintain consistent indentation (2 spaces)

### JavaScript Standards

- Use vanilla JavaScript (no frameworks)
- Use meaningful function names
- Add comments for complex logic
- Avoid global variables

## Git Commit Messages

Format:
```
type(scope): short description

Longer description if needed

Fixes #issue-number
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Example:
```
feat(cart): add quantity update functionality

- Added updateQuantity method to CartService
- Created form for quantity selection
- Updated cart template

Fixes #42
```

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Writing Tests

- Place tests in `src/test/java`
- Use meaningful test method names
- Follow Arrange-Act-Assert pattern
- Mock external dependencies

Example:
```java
@Test
void shouldCreateUserSuccessfully() {
    // Arrange
    UserRegistrationDto dto = new UserRegistrationDto();
    dto.setEmail("test@example.com");
    dto.setPassword("password123");

    // Act
    User result = userService.registerUser(dto);

    // Assert
    assertNotNull(result.getId());
    assertEquals("test@example.com", result.getEmail());
}
```

## Project Structure

```
src/
├── main/
│   ├── java/com/bookstore/
│   │   ├── config/       # Configuration classes
│   │   ├── controller/   # MVC controllers
│   │   ├── dto/          # Data transfer objects
│   │   ├── model/        # Entity classes
│   │   ├── repository/   # Data access layer
│   │   ├── service/      # Business logic
│   │   └── util/         # Utility classes
│   └── resources/
│       ├── static/       # CSS, JS, images
│       └── templates/    # Thymeleaf templates
└── test/
    └── java/com/bookstore/
```

## Areas for Contribution

### Good First Issues
- UI/UX improvements
- Documentation updates
- Adding test coverage
- Code refactoring

### Advanced Contributions
- Payment gateway integration
- Email notification system
- Performance optimization
- New book template features

## Review Process

1. All PRs require at least one review
2. CI checks must pass
3. Code coverage should not decrease
4. Documentation must be updated if needed

## Questions?

Feel free to:
- Open an issue for questions
- Join discussions
- Reach out to maintainers

Thank you for contributing!
