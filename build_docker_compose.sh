#!/bin/bash

# Stop and remove all containers
echo "Stopping and removing all containers..."
docker-compose down
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)

# Remove all images
echo "Removing all Docker images..."
docker rmi $(docker images -q)

# Billing Service
echo "Building and starting Billing Service..."
pushd ./billing-service
docker-compose up --build -d
popd

# Kafka
echo "Building and starting Kafka..."
pushd ./patient-management
docker-compose -f docker-compose-kafka.yml up --build -d
popd

# Analytics Service
echo "Building and starting Analytics Service..."
pushd ./analytics-service
docker-compose up --build -d
popd

# Doctor Service
echo "Building and starting Doctor Service..."
pushd ./doctor-service
docker-compose up --build -d
popd

# Main Application
echo "Building and starting Main Application..."
pushd ./patient-management
docker-compose up --build -d
popd

# API GATEWAY
echo "API Gateway Starting"
push ./api-gateway
docker-compose up --build -d
popd

echo "All services have been rebuilt and started!"
