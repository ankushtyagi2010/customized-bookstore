#!/bin/bash
# Helper script to run the Spring Boot application with proper configuration

# Source environment variables
source /usr/local/bin/setup-env.sh

echo "Starting Customized Bookstore Application..."
echo "MongoDB URI: ${SPRING_DATA_MONGODB_URI}"
echo "Profile: ${SPRING_PROFILES_ACTIVE}"
echo ""

# Unset JAVA_TOOL_OPTIONS to avoid debug port conflicts
unset JAVA_TOOL_OPTIONS

# Run the application
exec mvn spring-boot:run
