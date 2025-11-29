#!/bin/bash
# Environment setup script for devcontainer
# This script sets up environment variables for the bookstore application

# Function to check if MongoDB is accessible
check_mongodb_host() {
    timeout 1 bash -c "cat < /dev/null > /dev/tcp/$1/$2" 2>/dev/null
}

# Determine MongoDB connection string
if check_mongodb_host "mongodb" 27017; then
    export SPRING_DATA_MONGODB_URI="mongodb://mongodb:27017/bookstore"
elif check_mongodb_host "localhost" 27017; then
    export SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/bookstore"
fi

# Set other application environment variables
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"
export APP_UPLOAD_DIR="${APP_UPLOAD_DIR:-/app/uploads}"
export SPRING_DEVTOOLS_RESTART_ENABLED="${SPRING_DEVTOOLS_RESTART_ENABLED:-true}"

# Unset JAVA_TOOL_OPTIONS to avoid debug port conflicts when running manually
# The devcontainer docker-compose will set this for the service
if [ -n "$JAVA_TOOL_OPTIONS" ] && [[ "$JAVA_TOOL_OPTIONS" == *"jdwp"* ]]; then
    # Only unset if we're in an interactive shell (manual run)
    if [ -t 0 ]; then
        unset JAVA_TOOL_OPTIONS
    fi
fi
