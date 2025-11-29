# Devcontainer Setup for Customized Bookstore

This devcontainer configuration provides a complete development environment for the Customized Bookstore project.

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Visual Studio Code](https://code.visualstudio.com/)
- [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

## Getting Started

1. Open the project in VS Code
2. Press `F1` and select **Dev Containers: Reopen in Container**
3. Wait for the container to build and start (first time may take a few minutes)
4. Once loaded, the development environment is ready!

## What's Included

### Services

- **Java 17 Development Environment** - Full JDK with Maven
- **MongoDB 7.0** - Database service
- **Mongo Express** - Database management UI (optional)

### VS Code Extensions

- Java Extension Pack
- Spring Boot Tools
- Spring Boot Dashboard
- Lombok Support
- MongoDB for VS Code
- GitLens
- Code Spell Checker

### Ports

- `8080` - Spring Boot Application
- `5005` - Java Debug Port
- `27017` - MongoDB
- `8081` - Mongo Express (run with `--profile admin`)

### Git Configuration

Git is configured to work seamlessly with your host machine's credentials:

- **SSH Keys** - Your `~/.ssh` directory is mounted for git operations
- **Credential Helper** - Automatically configured to store credentials
- **GitLens** - Pre-installed for enhanced git visualization
- **Auto-fetch** - Enabled for staying up to date with remote changes

**First-Time Setup:**

When you first open the devcontainer, configure your git identity:

```bash
# Set your git identity (required on first use)
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

**What this means:**
- SSH keys for GitHub/GitLab work without additional setup
- Git credentials are stored securely within the container
- Once configured, git operations work seamlessly

**Verifying Git Setup:**
```bash
# Check git configuration
git config --list

# Test SSH connection to GitHub
ssh -T git@github.com

# Your credentials should work for all git operations
git pull
git push
```

## Development Workflow

### Running the Application

**Option 1: Using VS Code Tasks**
- Press `Ctrl+Shift+B` to build
- Press `F5` to run with debugger

**Option 2: Using Maven**
```bash
mvn spring-boot:run
```

**Option 3: Using Spring Boot Dashboard**
- Open Spring Boot Dashboard in VS Code sidebar
- Click the play button next to the application

### Running Tests

```bash
mvn test
```

Or use VS Code task: `Run Tests`

### Building

```bash
mvn clean install
```

Or use VS Code task: `Build with Maven`

### Debugging

1. Set breakpoints in your code
2. Press `F5` to start debugging
3. Or use the Debug panel and select "Debug Spring Boot App"

### Database Access

**MongoDB Connection String (from within container):**
```
mongodb://mongodb:27017/bookstore
```

**Mongo Express (Web UI):**
1. Start services with admin profile:
   ```bash
   docker-compose --profile admin up -d mongo-express
   ```
2. Access at: http://localhost:8081
3. Default credentials: `admin` / `admin123`

**Using MongoDB VS Code Extension:**
- The extension is pre-installed
- Connect using: `mongodb://mongodb:27017`

## Project Structure

```
/workspace          - Your project source code (mounted)
/app/uploads        - Upload directories
/home/vscode/.m2    - Maven cache (persistent)
```

## Useful Commands

```bash
# View application logs
mvn spring-boot:run

# Clean build
mvn clean install

# Run specific test
mvn test -Dtest=YourTestClass

# Check dependencies
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates
```

## Troubleshooting

### Container Won't Start
- Ensure Docker Desktop is running
- Check Docker Desktop has enough resources (recommended: 4GB RAM, 2 CPUs)
- Try rebuilding: `F1` → **Dev Containers: Rebuild Container**

### MongoDB Connection Issues
- Verify MongoDB is running: `docker ps`
- Check MongoDB health: `docker-compose ps`
- View MongoDB logs: `docker-compose logs mongodb`

### Port Already in Use
- Stop any local instances of the app or MongoDB
- Or change port mappings in `docker-compose.devcontainer.yml`

### Maven Dependencies Not Downloading
- Check internet connection
- Clear Maven cache: `rm -rf /home/vscode/.m2/repository`
- Rebuild container

### Git Issues

**SSH Key Permission Errors:**
```bash
# Fix SSH key permissions manually if needed
chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_rsa ~/.ssh/id_ed25519
```

**Git Config Issues:**
```bash
# If you see git config warnings or errors, configure git in the container:
git config --global user.name "Your Name"
git config --global user.email "your@email.com"

# Verify configuration
git config --list
```

**SSH Authentication Failed:**
- Verify SSH keys exist on host: `ls -la ~/.ssh`
- Test SSH agent on host: `ssh-add -l`
- Ensure your public key is added to GitHub/GitLab
- Rebuild container after adding keys

## Environment Variables

The following environment variables are pre-configured:

- `SPRING_PROFILES_ACTIVE=dev`
- `SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/bookstore`
- `APP_UPLOAD_DIR=/app/uploads`
- `SPRING_DEVTOOLS_RESTART_ENABLED=true`
- `JAVA_TOOL_OPTIONS` - Configured for remote debugging on port 5005

## Additional Resources

- [VS Code Dev Containers Documentation](https://code.visualstudio.com/docs/devcontainers/containers)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MongoDB Documentation](https://www.mongodb.com/docs/)
