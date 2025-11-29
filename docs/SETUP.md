# Setup Guide

This guide provides detailed instructions for setting up the CustomBooks application in different environments.

## Quick Start with VS Code Devcontainer (Recommended)

The fastest and most reliable way to get started is using VS Code devcontainers. This provides a fully configured development environment with zero configuration needed.

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Visual Studio Code](https://code.visualstudio.com/)
- [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/customized-bookstore.git
   cd customized-bookstore
   ```

2. **Open in VS Code:**
   ```bash
   code .
   ```

3. **Start devcontainer:**
   - Press `F1`
   - Select **Dev Containers: Reopen in Container**
   - Wait for container to build (3-5 minutes first time)

4. **You're ready!** The application will auto-build. To run:
   - Press `F5` to debug
   - Or run: `mvn spring-boot:run`

5. **Access services:**
   - Application: http://localhost:8080
   - MongoDB: mongodb://mongodb:27017
   - Mongo Express: http://localhost:8081 (optional)

### What's Included in Devcontainer

- ✅ Java 17 JDK with Maven
- ✅ MongoDB 7.0 database
- ✅ All VS Code extensions (Java, Spring Boot, Lombok, MongoDB, GitLens)
- ✅ Git configuration with credential forwarding
- ✅ Debugging pre-configured
- ✅ Hot reload with Spring DevTools
- ✅ MongoDB Express admin UI (optional)

For complete devcontainer documentation, see [.devcontainer/README.md](../.devcontainer/README.md)

---

## Traditional Setup (Without Devcontainer)

If you prefer to set up the development environment manually:

## Prerequisites

### Required Software

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 17+ | Runtime environment |
| Maven | 3.6+ | Build tool |
| MongoDB | 4.4+ | Database |
| Git | 2.0+ | Version control |

### Optional Software

| Software | Purpose |
|----------|---------|
| MongoDB Compass | GUI for MongoDB |
| IntelliJ IDEA / VS Code | IDE |
| Docker | Containerization |

---

## Installation Steps

### 1. Install Java 17

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
```

**macOS (Homebrew):**
```bash
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

**Windows:**
1. Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) or [Adoptium](https://adoptium.net/)
2. Run installer
3. Add to PATH environment variable

---

### 2. Install Maven

**Ubuntu/Debian:**
```bash
sudo apt install maven
mvn -version
```

**macOS (Homebrew):**
```bash
brew install maven
mvn -version
```

**Windows:**
1. Download from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract to a directory (e.g., `C:\Program Files\Maven`)
3. Add `bin` directory to PATH

---

### 3. Install MongoDB

**Ubuntu/Debian:**
```bash
# Import MongoDB public key
wget -qO - https://www.mongodb.org/static/pgp/server-6.0.asc | sudo apt-key add -

# Add repository
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/6.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list

# Install
sudo apt update
sudo apt install -y mongodb-org

# Start service
sudo systemctl start mongod
sudo systemctl enable mongod
```

**macOS (Homebrew):**
```bash
brew tap mongodb/brew
brew install mongodb-community@6.0
brew services start mongodb-community@6.0
```

**Windows:**
1. Download from [MongoDB Download Center](https://www.mongodb.com/try/download/community)
2. Run installer with default options
3. MongoDB runs as a Windows service

**Docker:**
```bash
docker run -d -p 27017:27017 --name mongodb mongo:6.0
```

---

### 4. Clone and Build Project

```bash
# Clone repository
git clone https://github.com/YOUR_USERNAME/customized-bookstore.git
cd customized-bookstore

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

---

## Configuration

### Application Properties

The main configuration file is `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/bookstore
      database: bookstore

app:
  upload:
    dir: ${user.home}/bookstore-uploads
```

### Environment Variables

You can override configuration using environment variables:

```bash
# MongoDB connection
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/bookstore

# Upload directory
export APP_UPLOAD_DIR=/custom/path/to/uploads

# Server port
export SERVER_PORT=8080
```

### MongoDB Configuration

For production with authentication:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://username:password@host:27017/bookstore?authSource=admin
```

---

## Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

Or with specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Mode

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/customized-bookstore-1.0.0.jar
```

With custom configuration:

```bash
java -jar target/customized-bookstore-1.0.0.jar \
  --spring.data.mongodb.uri=mongodb://prod-host:27017/bookstore \
  --server.port=80
```

---

## Docker Deployment

The project includes production-ready Docker configuration with multi-stage builds for optimized images.

### Dockerfile (Multi-Stage Build)

The included `Dockerfile` uses a multi-stage build process:

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security: non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Create upload directories
RUN mkdir -p /app/uploads/characters /app/uploads/covers \
    /app/uploads/user-uploads /app/uploads/pdfs && \
    chown -R appuser:appgroup /app

COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

The `docker-compose.yml` includes three services:

```yaml
services:
  # Spring Boot Application
  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: customized-bookstore
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/bookstore
      - APP_UPLOAD_DIR=/app/uploads
    volumes:
      - bookstore-uploads:/app/uploads
    depends_on:
      mongodb:
        condition: service_healthy
    networks:
      - bookstore-network
    restart: unless-stopped

  # MongoDB Database
  mongodb:
    image: mongo:7.0
    container_name: bookstore-mongodb
    ports:
      - "27017:27017"
    environment:
      - MONGO_INITDB_DATABASE=bookstore
    volumes:
      - mongodb-data:/data/db
      - mongodb-config:/data/configdb
    networks:
      - bookstore-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s

  # MongoDB Express (optional - admin profile)
  mongo-express:
    image: mongo-express:latest
    container_name: bookstore-mongo-express
    ports:
      - "8081:8081"
    environment:
      - ME_CONFIG_MONGODB_SERVER=mongodb
      - ME_CONFIG_BASICAUTH_USERNAME=admin
      - ME_CONFIG_BASICAUTH_PASSWORD=admin123
    depends_on:
      mongodb:
        condition: service_healthy
    profiles:
      - admin

networks:
  bookstore-network:
    driver: bridge

volumes:
  mongodb-data:
  mongodb-config:
  bookstore-uploads:
```

### Run with Docker Compose

```bash
# Build and start all services
docker-compose up -d --build

# Build with no cache (after code changes)
docker-compose build --no-cache app
docker-compose up -d

# View logs
docker-compose logs -f app

# View container status
docker-compose ps

# Stop all services
docker-compose down

# Start with MongoDB admin UI
docker-compose --profile admin up -d
```

### Updating After Code Changes

When you make changes to CSS, HTML, or Java files:

```bash
# Rebuild and restart the application
docker-compose down
docker-compose build --no-cache app
docker-compose up -d

# Verify containers are healthy
docker-compose ps
```

---

## Initial Setup

### Default Users

On first run, the application creates:

| Email | Password | Role |
|-------|----------|------|
| admin@custombooks.com | admin123 | ADMIN |
| customer@example.com | customer123 | CUSTOMER |

### Sample Data

The `DataInitializer` class creates:
- 2 default users
- 5 sample character images
- 3 book templates (Adventure, Birthday, Bedtime)

### Adding Custom Images

1. **Through Admin Panel:**
   - Login as admin
   - Navigate to `/admin/characters`
   - Upload character images

2. **Manually:**
   - Place images in `src/main/resources/static/images/`
   - Or upload to the configured upload directory

---

## Troubleshooting

### MongoDB Connection Issues

```bash
# Check if MongoDB is running
sudo systemctl status mongod

# Check connection
mongosh --eval "db.runCommand({ connectionStatus: 1 })"
```

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or run on different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

### Maven Build Failures

```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Force update dependencies
mvn clean install -U
```

### Permission Issues with Uploads

```bash
# Create upload directory
mkdir -p ~/bookstore-uploads

# Set permissions
chmod 755 ~/bookstore-uploads
```

---

## IDE Setup

### IntelliJ IDEA

1. Open project: `File > Open > select pom.xml`
2. Enable annotation processing: `Settings > Build > Compiler > Annotation Processors`
3. Install Lombok plugin: `Settings > Plugins > Lombok`
4. Run: Right-click `BookstoreApplication.java` > Run

### VS Code

1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. Open project folder
3. Run from Spring Boot Dashboard or terminal

---

## Health Check

After starting the application, verify it's working:

```bash
# Check if application is running
curl http://localhost:8080/

# Check actuator health (if enabled)
curl http://localhost:8080/actuator/health
```

Access the application at: http://localhost:8080
