#!/bin/bash
# MongoDB startup script for devcontainer
# This script checks if MongoDB is available via docker-compose service
# If not, it starts MongoDB locally

set -e

echo "Checking MongoDB availability..."

# Function to check if MongoDB is accessible
check_mongodb() {
    timeout 2 bash -c 'cat < /dev/null > /dev/tcp/mongodb/27017' 2>/dev/null
}

# Function to check if MongoDB is running locally
check_local_mongodb() {
    timeout 2 bash -c 'cat < /dev/null > /dev/tcp/localhost/27017' 2>/dev/null
}

# Function to check if mongod process is running
is_mongod_running() {
    pgrep -x mongod >/dev/null 2>&1
}

# Check if docker-compose MongoDB service is available
if check_mongodb; then
    echo "✓ MongoDB service is accessible at mongodb:27017"
    echo "Application will use: mongodb://mongodb:27017/bookstore"
    exit 0
fi

# Check if MongoDB is already running locally
if check_local_mongodb || is_mongod_running; then
    echo "✓ MongoDB is already running locally"
    echo "Application will use: mongodb://localhost:27017/bookstore"
    exit 0
fi

# Start MongoDB locally
echo "Starting MongoDB locally..."
sudo mkdir -p /data/db /var/log/mongodb
sudo chown -R mongodb:mongodb /data/db /var/log/mongodb

# Start mongod directly (not as a service since systemd isn't available)
sudo -u mongodb mongod --dbpath /data/db \
    --logpath /var/log/mongodb/mongod.log \
    --fork \
    --bind_ip_all \
    2>&1 | grep -v "Killing" || true

# Wait for MongoDB to be ready
echo "Waiting for MongoDB to be ready..."
for i in {1..30}; do
    if mongosh --quiet --eval "db.adminCommand('ping')" >/dev/null 2>&1; then
        echo "✓ MongoDB is ready!"
        echo ""
        echo "MongoDB is running locally on port 27017"
        echo "Application will use: mongodb://localhost:27017/bookstore"
        echo ""
        echo "To connect manually:"
        echo "  mongosh mongodb://localhost:27017/bookstore"
        exit 0
    fi
    sleep 1
done

echo "ERROR: MongoDB failed to start within 30 seconds"
echo "Check logs: sudo cat /var/log/mongodb/mongod.log"
exit 1
