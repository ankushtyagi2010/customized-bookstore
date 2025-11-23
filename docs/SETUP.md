# Setup Guide

This guide provides detailed instructions for setting up the CustomBooks application in different environments.

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

### Dockerfile

Create `Dockerfile` in project root:

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/customized-bookstore-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/bookstore
    depends_on:
      - mongo
    volumes:
      - uploads:/app/uploads

  mongo:
    image: mongo:6.0
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data:
  uploads:
```

### Run with Docker Compose

```bash
# Build and run
mvn clean package -DskipTests
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop
docker-compose down
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
