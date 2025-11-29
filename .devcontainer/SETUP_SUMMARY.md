# MongoDB Setup Summary

This document summarizes all the automated MongoDB setup that will occur when you rebuild the devcontainer.

## What Happens Automatically

### On Container Build (Dockerfile)

1. **MongoDB 7.0 Installation**
   - Installed via apt package manager
   - Includes `mongod`, `mongosh`, and database tools
   - Located at: `/usr/bin/mongod`

2. **Directory Setup**
   - Data directory: `/data/db`
   - Log directory: `/var/log/mongodb`
   - Ownership: `mongodb:mongodb`

3. **Scripts Installed**
   - `/usr/local/bin/start-mongodb.sh` - Starts MongoDB automatically
   - `/usr/local/bin/setup-env.sh` - Sets environment variables
   - `/usr/local/bin/run-app.sh` - Helper to run the application

4. **Permissions Configured**
   - Node user can run MongoDB as mongodb user (sudo)
   - Node user can create/modify MongoDB directories (sudo)
   - No password required for MongoDB operations

5. **Shell Configuration**
   - Environment variables auto-loaded in bash and zsh
   - Helpful aliases added:
     - `run-app` - Start the Spring Boot application
     - `mongo-start` - Manually start MongoDB
     - `mongo-check` - Verify MongoDB is running

### On Container Start (postStartCommand)

The `/usr/local/bin/start-mongodb.sh` script runs automatically and:

1. **Checks for docker-compose MongoDB service**
   - If available at `mongodb:27017`, uses that
   - Sets connection: `mongodb://mongodb:27017/bookstore`

2. **Falls back to local MongoDB**
   - If service not found, starts local instance
   - Creates directories if needed
   - Starts `mongod` process
   - Waits for MongoDB to be ready (up to 30 seconds)
   - Sets connection: `mongodb://localhost:27017/bookstore`

### On Container Attach (postAttachCommand)

Same MongoDB startup script runs when you reattach to an existing container, ensuring MongoDB is always available.

## Environment Variables

The following variables are automatically set when you open a shell:

```bash
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/bookstore  # (or mongodb://mongodb:27017/bookstore)
SPRING_PROFILES_ACTIVE=dev
APP_UPLOAD_DIR=/app/uploads
SPRING_DEVTOOLS_RESTART_ENABLED=true
```

The `JAVA_TOOL_OPTIONS` variable is automatically unset in interactive shells to prevent debug port conflicts.

## How to Use

### Start the Application

Simply run:
```bash
run-app
```

Or manually:
```bash
mvn spring-boot:run
```

The environment is automatically configured!

### Check MongoDB Status

```bash
mongo-check
```

Or manually:
```bash
mongosh --eval "db.adminCommand('ping')"
```

### Connect to MongoDB

```bash
mongosh mongodb://localhost:27017/bookstore
```

### View MongoDB Logs

```bash
sudo cat /var/log/mongodb/mongod.log
```

## Files Modified for Persistence

All these changes survive container rebuilds:

### Configuration Files
- `.devcontainer/Dockerfile` - MongoDB installation and setup
- `.devcontainer/devcontainer.json` - Auto-start configuration
- `.devcontainer/docker-compose.devcontainer.yml` - Docker services (already had MongoDB)

### Scripts Created
- `.devcontainer/start-mongodb.sh` - MongoDB startup automation
- `.devcontainer/setup-env.sh` - Environment variable configuration
- `.devcontainer/run-app.sh` - Application startup helper

### Documentation
- `.devcontainer/README.md` - Updated with MongoDB information
- `.devcontainer/SETUP_SUMMARY.md` - This file

## Verification

After rebuilding the devcontainer, verify everything works:

1. **MongoDB is running:**
   ```bash
   mongo-check
   ```
   Expected output: `{ ok: 1 }` and "MongoDB is running"

2. **Environment variables are set:**
   ```bash
   echo $SPRING_DATA_MONGODB_URI
   ```
   Expected output: `mongodb://localhost:27017/bookstore`

3. **Application starts:**
   ```bash
   run-app
   ```
   Expected: Application starts and connects to MongoDB

4. **Database has data:**
   ```bash
   mongosh bookstore --eval "db.users.countDocuments()"
   ```
   Expected: `2` (after DataInitializer runs)

## Troubleshooting

### MongoDB won't start
```bash
# Check if already running
ps aux | grep mongod

# Check logs
sudo cat /var/log/mongodb/mongod.log

# Manually start
mongo-start
```

### Environment variables not set
```bash
# Manually source the script
source /usr/local/bin/setup-env.sh

# Verify
echo $SPRING_DATA_MONGODB_URI
```

### Application can't connect
```bash
# Verify MongoDB is accessible
mongo-check

# Check connection string
echo $SPRING_DATA_MONGODB_URI

# Try manual connection
mongosh $SPRING_DATA_MONGODB_URI
```

## What You Don't Need to Do Anymore

After rebuilding the devcontainer, you **DO NOT** need to:

- ❌ Install MongoDB manually
- ❌ Start MongoDB manually
- ❌ Set environment variables
- ❌ Configure connection strings
- ❌ Create database directories
- ❌ Set up permissions
- ❌ Remember complex Maven commands

Everything is **automatic**! Just rebuild and start coding.
