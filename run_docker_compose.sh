#!/bin/bash

# Billing Service
echo "Starting Billing Service..."
pushd ./billing-service
docker-compose up -d
popd

# Kafka
echo "Starting Kafka..."
pushd ./patient-management
docker-compose -f docker-compose-kafka.yml up -d
popd

# Analytics Service
echo "Starting Analytics Service..."
pushd ./analytics-service
docker-compose up -d
popd

# Doctor Service
echo "Building and starting Doctor Service..."
pushd ./doctor-service
docker-compose up -d
popd

# Main
echo "Starting Main Application..."
pushd ./patient-management
docker-compose up -d
popd

echo "All Services Are Up!"
