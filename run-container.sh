#!/bin/bash

# Helper script to run Claude Code container

CONTAINER_NAME="customized-bookstore-dev"
IMAGE_NAME="customized-bookstore-image"

# Function to check if container is running
is_running() {
    docker ps -q -f name=$CONTAINER_NAME 2>/dev/null
}

# Function to check if container exists
exists() {
    docker ps -aq -f name=$CONTAINER_NAME 2>/dev/null
}

# Start container if not running
if [ -z "$(is_running)" ]; then
    if [ -n "$(exists)" ]; then
        echo "Starting existing container..."
        docker start $CONTAINER_NAME
    else
        echo "Creating and starting new container..."
        docker run -d \
            --name $CONTAINER_NAME \
            --cap-add=NET_ADMIN \
            --cap-add=NET_RAW \
            -v "$(pwd):/workspace" \
            $IMAGE_NAME \
            tail -f /dev/null

        # Initialize firewall
        echo "Initializing firewall..."
        docker exec -u node $CONTAINER_NAME sudo /usr/local/bin/init-firewall.sh 2>&1 | grep -v "already added" || true
    fi
fi

# Execute command or open shell
if [ $# -eq 0 ]; then
    echo "Opening shell in container..."
    docker exec -it -u node $CONTAINER_NAME zsh
else
    docker exec -u node $CONTAINER_NAME "$@"
fi