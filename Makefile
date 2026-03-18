# Patient Management System - Makefile
# Optimized for WSL/Linux and Windows compatibility

# Force Bash shell for consistency across environments
SHELL := /bin/bash

# Variables
DOCKER := docker

# Detect 'docker compose' vs 'docker-compose'
# Prioritize the modern plugin version which handles WSL credentials better
DOCKER_COMPOSE := $(shell docker compose version >/dev/null 2>&1 && echo "docker compose" || echo "docker-compose")

SERVICES := api-gateway patient-management auth-service doctor-service appointment-service billing-service analytics-service

.PHONY: help
help:
	@echo "╔══════════════════════════════════════════════════════════╗"
	@echo "║  Patient Management System - Development Commands        ║"
	@echo "╚══════════════════════════════════════════════════════════╝"
	@echo ""
	@echo "📦 LOCAL DEVELOPMENT (Docker Compose):"
	@echo "  make dev-up                - Start all services locally with Docker Compose"
	@echo "  make dev-down              - Stop all local services"
	@echo "  make dev-logs              - View logs from all services"
	@echo "  make dev-logs-service SVC  - View logs from specific service (e.g., SVC=patient-management)"
	@echo "  make dev-build             - Build all Docker images locally"
	@echo "  make dev-rebuild           - Rebuild all Docker images from scratch"
	@echo ""
	@echo "🏗️  BUILD COMMANDS:"
	@echo "  make build-all             - Build all services"
	@echo "  make build-service SVC     - Build specific service"
	@echo ""
	@echo "🧹 CLEANUP:"
	@echo "  make clean-local           - Remove all local Docker containers and volumes"
	@echo ""

# ============================================================================
# LOCAL DEVELOPMENT WITH DOCKER COMPOSE
# ============================================================================

.PHONY: dev-up
dev-up:
	@echo "Starting all services with Docker Compose..."
	$(DOCKER_COMPOSE) -f docker-compose.yml up -d
	@echo "Services started!"
	@echo "Services available at:"
	@echo "   API Gateway:       http://localhost:4004"
	@echo ""

.PHONY: dev-down
dev-down:
	@echo "Stopping all services..."
	$(DOCKER_COMPOSE) -f docker-compose.yml down
	@echo "Services stopped!"

.PHONY: dev-logs
dev-logs:
	$(DOCKER_COMPOSE) -f docker-compose.yml logs -f

.PHONY: dev-logs-service
dev-logs-service:
	@if [ -z "$(SVC)" ]; then \
		echo "Error: Please specify SVC=<service-name>"; \
		echo "   Example: make dev-logs-service SVC=patient-management"; \
		exit 1; \
	fi
	$(DOCKER_COMPOSE) -f docker-compose.yml logs -f $(SVC)

.PHONY: dev-build
dev-build:
	@echo "Building Docker images..."
	$(DOCKER_COMPOSE) -f docker-compose.yml build
	@echo "Build complete!"

.PHONY: dev-rebuild
dev-rebuild:
	@echo "Rebuilding Docker images (no cache)..."
	$(DOCKER_COMPOSE) -f docker-compose.yml build --no-cache
	@echo " Rebuild complete!"

# ============================================================================
# BUILD COMMANDS
# ============================================================================

.PHONY: build-all
build-all:
	@echo " Building all services with Maven..."
	@for svc in $(SERVICES); do \
		echo "----------------------------------------------------------------"; \
		echo "Building $$svc..."; \
		(cd $$svc && mvn clean package -DskipTests) || exit 1; \
	done
	@echo " All services built!"

.PHONY: build-service
build-service:
	@if [ -z "$(SVC)" ]; then \
		echo "Error: Please specify SVC=<service-directory>"; \
		echo "   Example: make build-service SVC=auth-service"; \
		exit 1; \
	fi
	@echo "Building $(SVC)..."
	cd $(SVC) && mvn clean package -DskipTests
	@echo " $(SVC) built!"

# ============================================================================
# CLEANUP
# ============================================================================

.PHONY: clean-local
clean-local:
	@echo "Cleaning up Docker resources..."
	$(DOCKER_COMPOSE) -f docker-compose.yml down -v
	$(DOCKER) system prune -f
	@echo " Local cleanup complete!"

.PHONY: clean-all
clean-all: clean-local
	@echo " Complete cleanup done!"

# ============================================================================
# UTILITY COMMANDS
# ============================================================================

.PHONY: ping
ping:
	@echo "Pong!"

.PHONY: status
status:
	@echo "Checking services status..."
	@if command -v $(DOCKER) > /dev/null 2>&1; then \
		echo -n "Docker: "; \
		$(DOCKER) ps -q | wc -l | tr -d ' ' | xargs echo -n; \
		echo " containers running"; \
	else \
		echo "Docker: Not found"; \
	fi

.PHONY: info
info:
	@echo "Project Information:"
	@echo "   Services:  $(SERVICES)"