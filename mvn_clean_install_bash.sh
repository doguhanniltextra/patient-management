#!/bin/bash

# Billing Service
echo "Cleaning Billing Service..."
pushd ./billing-service
mvn clean install
popd

# Patient
echo "Cleaning Patient Management..."
pushd ./patient-management
mvn clean install
popd

# Analytics Service
echo "Cleaning Analytics Service..."
pushd ./analytics-service
mvn clean install
popd

# Doctor Service
echo "Cleaning Doctor Service..."
pushd ./doctor-service
mvn clean install
popd

echo "All Services Are Up!"
