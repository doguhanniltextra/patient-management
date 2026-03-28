# Design Pattern Analysis — Patient Management System

> A comprehensive breakdown of every design pattern used in this project,
> covering architecture, code structure, security, messaging, and infrastructure.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Architectural Patterns](#1-architectural-patterns)
   - [Microservices Architecture](#11-microservices-architecture)
   - [API Gateway Pattern](#12-api-gateway-pattern)
   - [Event-Driven Architecture](#13-event-driven-architecture-eda)
3. [Structural Patterns](#2-structural-patterns)
   - [Layered Architecture](#21-layered-architecture-per-service)
   - [DTO Pattern](#22-dto-pattern-data-transfer-object)
   - [Mapper Pattern](#23-mapper-pattern)
   - [Repository Pattern](#24-repository-pattern)
4. [Behavioral Patterns](#3-behavioral-patterns)
   - [Chain of Responsibility](#31-chain-of-responsibility-filter-chain)
   - [Observer Pattern (Kafka)](#32-observer-pattern-via-kafka)
   - [Validator Pattern](#33-validator-pattern)
5. [Security Patterns](#4-security-patterns)
   - [Stateless JWT Authentication](#41-stateless-jwt-authentication)
   - [Hybrid RBAC / ABAC](#42-hybrid-rbac--abac)
   - [Defense in Depth](#43-defense-in-depth)
6. [Cross-Cutting Concern Patterns](#5-cross-cutting-concern-patterns)
   - [Global Exception Handler](#51-global-exception-handler)
   - [Constants Pattern](#52-constants-pattern)
   - [Observability Stack](#53-observability-stack)
7. [Infrastructure Patterns](#6-infrastructure-patterns)
   - [Containerization](#61-containerization)
   - [Shared Database (Anti-Pattern)](#62-shared-database-anti-pattern-note)
8. [Summary Table](#summary-table)

---

## Architecture Overview

The system is a **Spring Boot microservices** application with six services
communicating through an API Gateway and Apache Kafka:

```
                         ┌───────────────────────┐
                         │    Client (Browser)    │
                         └───────────┬───────────┘
                                     │
                         ┌───────────▼───────────┐
                         │   API Gateway (:4004)  │
                         │  ┌─────────────────┐   │
                         │  │  JWT WebFilter   │   │
                         │  │  Route Config    │   │
                         │  └─────────────────┘   │
                         └───┬───┬───┬───┬───┬───┘
                             │   │   │   │   │
              ┌──────────────┘   │   │   │   └──────────────┐
              │          ┌───────┘   │   └───────┐          │
              ▼          ▼           ▼           ▼          ▼
        ┌──────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
        │  Auth    │ │Patient │ │ Doctor │ │Appoint.│ │Billing │
        │ :8089   │ │ :8080  │ │ :8083  │ │ :8084  │ │ :8081  │
        └────┬─────┘ └──┬──┬──┘ └──┬─────┘ └──┬──┬──┘ └──┬─────┘
             │          │  │       │           │  │       │
             │          │  │       │           │  │       │
        ┌────▼──────────▼──┼───────▼───────────┼──┼───────┘
        │   PostgreSQL     │                   │  │
        │   (:5432)        │                   │  │
        └──────────────────┘                   │  │
                                               │  │
        ┌──────────────────────────────────────┘  │
        │              Apache Kafka               │
        │              (:9092)                    │
        │                                         │
        │  Topics:                                │
        │   • patient       (CREATED / DELETED)   │
        │   • appointment   (PAYMENT_UPDATED)     │
        └──────────────────────────────────────────┘
                    │
        ┌───────────▼───────────┐
        │  Prometheus (:9090)   │
        │  Grafana    (:3000)   │
        └───────────────────────┘
```

---

## 1. Architectural Patterns

### 1.1 Microservices Architecture

The system is decomposed into **six independently deployable services**,
each responsible for a distinct bounded context:

| Service              | Port | Responsibility                      |
|----------------------|------|-------------------------------------|
| `api-gateway`        | 4004 | Routing and authentication gateway  |
| `auth-service`       | 8089 | User registration, login, JWT issue |
| `patient-management` | 8080 | Patient CRUD and medical records    |
| `doctor-service`     | 8083 | Doctor profiles                     |
| `appointment-service`| 8084 | Appointment scheduling              |
| `billing-service`    | 8081 | Invoice generation                  |

Each service has:

- Its own Spring Boot `Application.java` entry point
- Its own `Dockerfile`
- Its own package structure (`controller/`, `service/`, `repository/`, etc.)

**Why this matters:** Each service can be developed, tested, deployed, and scaled
independently. A change in the billing logic does not require re-deploying the
patient service.

---

### 1.2 API Gateway Pattern

**File:** `api-gateway/src/main/java/com/project/config/SecurityConfig.java`

The `api-gateway` service acts as the **single entry point** for all external
client requests. Built on **Spring Cloud Gateway** (WebFlux-based), it handles:

1. **Centralized JWT validation** — tokens are checked before any request reaches
   a downstream service.
2. **Route-based proxying** — requests are forwarded to backend services via
   environment-configured URLs (e.g., `PATIENT_SERVICE_URL=http://patient-management:8080`).
3. **Cross-cutting auth enforcement** — downstream services don't need to be
   publicly exposed; only the gateway port (4004) is needed.

```
Client  ──►  API Gateway (:4004)  ──►  Patient Service (:8080)
                 │
                 ├──►  Doctor Service (:8083)
                 ├──►  Appointment Service (:8084)
                 └──►  Billing Service (:8081)
```

---

### 1.3 Event-Driven Architecture (EDA)

The system uses **Apache Kafka** for asynchronous, loosely-coupled inter-service
communication.

#### Producers (who sends events)

| Service              | Event Type                  | Kafka Topic     |
|----------------------|-----------------------------|-----------------|
| `patient-management` | `PATIENT_CREATED`           | `patient`       |
| `patient-management` | `PATIENT_DELETED`           | `patient`       |
| `appointment-service`| `APPOINTMENT_PAYMENT_UPDATED`| `appointment`  |

**File:** `patient-management/src/main/java/.../kafka/KafkaProducer.java`

#### Consumers (who listens for events)

| Service           | Listens To                    | Action                  |
|-------------------|-------------------------------|-------------------------|
| `billing-service` | `APPOINTMENT_PAYMENT_UPDATED` | Auto-generates invoices |

**File:** `billing-service/src/main/java/.../kafka/KafkaConsumer.java`

The pattern used is **fire-and-forget** with `CompletableFuture` callbacks.
The producer does NOT wait for the consumer to process the message:

```java
CompletableFuture<?> future = kafkaTemplate.send("patient", json);
future.whenComplete((result, ex) -> {
    if (ex != null) {
        log.error("KAFKA: Failed to send event", ex);
    } else {
        log.info("KAFKA: Event sent successfully");
    }
});
```

---

## 2. Structural Patterns

### 2.1 Layered Architecture (per service)

Every microservice internally follows a strict **3-layer architecture**:

```
┌─────────────────────────────────┐
│          Controller             │   ← HTTP endpoints, @PreAuthorize
│    (REST API + Swagger docs)    │
├─────────────────────────────────┤
│           Service               │   ← Business logic, orchestration
│    (Transaction management)     │
├─────────────────────────────────┤
│          Repository             │   ← Data access (Spring Data JPA)
│    (Database queries)           │
└─────────────────────────────────┘

  Supporting components:
  ┌────────────┐  ┌────────────┐  ┌────────────┐
  │   Model    │  │    DTO     │  │   Helper   │
  │ (Entities) │  │ (Request/  │  │ (Mapper +  │
  │            │  │  Response) │  │  Validator) │
  └────────────┘  └────────────┘  └────────────┘
```

| Layer          | Package        | Responsibility                                |
|----------------|----------------|-----------------------------------------------|
| **Controller** | `controller/`  | HTTP endpoints, request/response mapping      |
| **Service**    | `service/`     | Business logic, orchestration                 |
| **Repository** | `repository/`  | Data access via Spring Data JPA               |
| **Model**      | `model/`       | JPA entities with validation annotations      |
| **DTO**        | `dto/request/` | Layer-specific data transfer objects          |
|                | `dto/response/`|                                               |
| **Helper**     | `helper/`      | Mapper and Validator components               |

---

### 2.2 DTO Pattern (Data Transfer Object)

The project uses **separate DTOs for each layer boundary**, with distinct
request and response variants. This prevents JPA entities from leaking
into API responses.

```
    Controller Layer                    Service Layer                    Kafka Layer
  ┌──────────────────┐              ┌──────────────────┐            ┌──────────────────┐
  │ CreatePatient    │   Mapper     │ CreatePatient    │            │ KafkaPatient     │
  │ Controller       │ ─────────►  │ Service          │  Mapper    │ RequestDto       │
  │ RequestDto       │              │ RequestDto       │ ────────► │                  │
  └──────────────────┘              └──────────────────┘            └──────────────────┘
  ┌──────────────────┐              ┌──────────────────┐
  │ CreatePatient    │   ◄─────    │ CreatePatient    │
  │ Controller       │   Mapper    │ Service          │
  │ ResponseDto      │              │ ResponseDto      │
  └──────────────────┘              └──────────────────┘
```

Example DTO naming convention:

- `CreatePatientControllerRequestDto` → incoming HTTP request
- `CreatePatientServiceRequestDto` → passed to service layer
- `CreatePatientServiceResponseDto` → returned from service layer
- `KafkaPatientRequestDto` → sent to Kafka topic

---

### 2.3 Mapper Pattern

**File:** `patient-management/src/main/java/.../helper/UserMapper.java`

A `@Component` class centralizes **all object-to-object transformation**:

```java
@Component
public class UserMapper {

    // Controller DTO → Service DTO
    public CreatePatientServiceRequestDto getCreatePatientServiceRequestDto(
            CreatePatientControllerRequestDto controllerDto) { ... }

    // Entity → Service Response DTO
    public GetPatientServiceResponseDto toServiceResponseDto(Patient patient) { ... }

    // Entity → Kafka DTO
    public KafkaPatientRequestDto getKafkaPatientRequestDto(Patient patient) { ... }
}
```

This keeps mapping logic **out of controllers and services**, making both easier
to read and test.

---

### 2.4 Repository Pattern

**File:** `patient-management/src/main/java/.../repository/PatientRepository.java`

Spring Data JPA repositories abstract database operations behind simple
interface method signatures:

```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
    Patient findByEmail(String email);
}
```

No SQL or JPQL is written manually — Spring generates the queries from
the method names at runtime.

---

## 3. Behavioral Patterns

### 3.1 Chain of Responsibility (Filter Chain)

The **Spring Security filter chain** implements the Chain of Responsibility pattern.
Each filter processes the request and decides whether to pass it to the next filter
or reject it.

This pattern is used in **two places**:

#### At the API Gateway (WebFlux)

```java
// api-gateway SecurityConfig.java
@Bean
public WebFilter jwtAuthenticationFilter() {
    return (exchange, chain) -> {
        // 1. Extract JWT from Authorization header
        // 2. Validate token and extract claims
        // 3. Set authentication in SecurityContext
        // 4. Pass to next filter: chain.filter(exchange)
    };
}
```

#### At Each Downstream Service (Servlet)

```java
// patient-management JwtAuthFilter.java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(...) {
        // 1. Extract JWT from Authorization header
        // 2. Validate token and extract claims
        // 3. Set authentication in SecurityContextHolder
        // 4. Pass to next filter: filterChain.doFilter(request, response)
    }
}
```

---

### 3.2 Observer Pattern (via Kafka)

Services are **loosely coupled** through Kafka topics. The billing service
"observes" appointment events without any direct code dependency on the
appointment service:

```
  Appointment Service                     Billing Service
  ┌──────────────────┐                   ┌──────────────────┐
  │  Publishes event │                   │  Listens for     │
  │  to Kafka topic  │ ───►  Kafka  ───► │  events and      │
  │  "appointment"   │                   │  generates       │
  │                  │                   │  invoices         │
  └──────────────────┘                   └──────────────────┘

  • No direct HTTP call between the two services
  • Billing Service can be down, and events will queue in Kafka
  • New consumers can be added without changing the producer
```

---

### 3.3 Validator Pattern

**File:** `patient-management/src/main/java/.../helper/UserValidator.java`

Business validation logic is extracted into dedicated `@Component` classes,
keeping the service layer focused on orchestration:

```java
@Component
public class UserValidator {

    // Throws EmailAlreadyExistsException if duplicate
    public void CheckEmailIsExistsOrNotForCreatePatient(...) { ... }

    // Throws PatientNotFoundException if not found
    public Patient getPatientForUpdateMethod(UUID id, ...) { ... }

    // Date parsing with meaningful error messages
    private LocalDate parseDateForCreatePatient(String dateStr) { ... }
}
```

**Similarly in auth-service:**
`AuthValidator.java` handles username uniqueness checks and password validation.

---

## 4. Security Patterns

### 4.1 Stateless JWT Authentication

The auth service issues **JSON Web Tokens** containing user identity and
role claims:

```
JWT Payload:
{
  "sub": "john_doe",              ← username
  "userId": "550e8400-...",       ← user's UUID
  "roles": ["ADMIN", "DOCTOR"],   ← assigned roles
  "iat": 1711670400,              ← issued at
  "exp": 1711756800               ← expires in 24 hours
}
```

**Key characteristics:**

- Signed with **HMAC-SHA256** using a shared secret (`app.secret`)
- **No server-side sessions** — `SessionCreationPolicy.STATELESS` is set
  in every service's `SecurityConfig`
- Token is passed in the `Authorization: Bearer <token>` header

**File:** `auth-service/src/main/java/.../service/JwtService.java`

---

### 4.2 Hybrid RBAC / ABAC

The project combines two access control models:

#### RBAC (Role-Based Access Control)

Access is granted based on the user's **role** (extracted from the JWT):

```java
// Only admins can delete patients
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deletePatient(@PathVariable UUID id) { ... }

// Doctors, admins, and receptionists can view patients
@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'RECEPTIONIST')")
public ResponseEntity<Page<...>> getPatients(...) { ... }
```

#### ABAC (Attribute-Based Access Control) — Ownership Checks

Access is also granted based on **resource ownership**:

```java
// Admins/receptionists by role OR the patient themselves
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @securityService.isPatientOwner(authentication, #id)")
public ResponseEntity<...> updatePatient(@PathVariable UUID id, ...) { ... }
```

**File:** `patient-management/src/main/java/.../security/SecurityOwnershipService.java`

```java
@Service("securityService")
public class SecurityOwnershipService {
    public boolean isPatientOwner(Authentication authentication, UUID patientId) {
        UUID tokenUserId = UUID.fromString(authentication.getName());
        return patientId.equals(tokenUserId);
    }
}
```

---

### 4.3 Defense in Depth

JWT validation happens at **two layers**:

```
  Client  ──►  API Gateway (validates JWT)  ──►  Patient Service (validates JWT again)
                    │                                      │
                    │  Layer 1: Gateway filter              │  Layer 2: Service filter
                    │  Rejects if token is invalid          │  Rejects if token is invalid
```

**Why this matters:** Even if someone bypasses the gateway (e.g., makes a direct
call to `patient-management:8080` within the Docker network), the service itself
will still reject unauthenticated requests.

---

## 5. Cross-Cutting Concern Patterns

### 5.1 Global Exception Handler

**File:** `patient-management/src/main/java/.../exception/GlobalExceptionHandler.java`

Uses Spring's `@ControllerAdvice` to centralize error handling across all
controllers in the service:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(...) {
        // Returns 400 with field-level error messages
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(...) {
        // Returns 400 with "Email address already exists"
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundException(...) {
        // Returns 400 with "Patient not found"
    }
}
```

---

### 5.2 Constants Pattern

Endpoint paths, log messages, and Swagger descriptions are extracted into
dedicated constant classes to prevent **magic strings**:

```
constants/
├── Endpoints.java        ← URL paths ("/api/patients", "/{id}", etc.)
├── LogMessages.java       ← Log message templates
├── SwaggerMessages.java   ← API documentation strings
└── KafkaTopics.java       ← Topic names ("patient", "appointment")
```

This makes it easy to:

- Refactor URL paths in a single place
- Keep log messages consistent
- Search for all references to a constant

---

### 5.3 Observability Stack

```
  Services ────►  /actuator/prometheus  ────►  Prometheus (:9090)  ────►  Grafana (:3000)
                                                  (scrapes metrics)         (dashboards)
```

- **Spring Boot Actuator** exposes health and metrics endpoints on every service
- **Prometheus** scrapes these endpoints at regular intervals
- **Grafana** provides dashboards for visualization
- **SLF4J** structured logging is used throughout all services

---

## 6. Infrastructure Patterns

### 6.1 Containerization

Every service has its own `Dockerfile`; all services are orchestrated via
`docker-compose.yml` with:

- **Health checks** — `curl` for Spring services, `pg_isready` for PostgreSQL
- **Resource limits** — CPU and memory caps per container
- **Named volumes** — persistent storage for PostgreSQL, Kafka, Prometheus, Grafana
- **Isolated networking** — all containers communicate on a `patient-network` bridge

```yaml
# Example resource limits (from docker-compose.yml)
x-java-service-resources: &java-resources
  deploy:
    resources:
      limits:
        cpus: "1.0"
        memory: 512M
      reservations:
        cpus: "0.25"
        memory: 256M
```

---

### 6.2 Shared Database (Anti-Pattern Note)

> **⚠️ Warning:** All six services connect to the **same PostgreSQL instance**
> (`patient_db` on port 5432). While this simplifies the development setup,
> it violates the microservices principle of **"Database per Service"**.
>
> **Risks:**
> - Schema changes in one service can break others
> - No independent scaling of data stores
> - Tight coupling at the data layer
>
> **Recommendation:** In production, each service should own its own database
> (or at minimum, its own schema within the shared instance).

---

## Summary Table

| Pattern                        | Category       | Where It's Used                                    |
|--------------------------------|----------------|----------------------------------------------------|
| Microservices Architecture     | Architecture   | 6 independent Spring Boot services                 |
| API Gateway                    | Architecture   | `api-gateway` (Spring Cloud Gateway / WebFlux)     |
| Event-Driven (Kafka)           | Architecture   | Patient → Kafka → Billing                          |
| Layered Architecture           | Structural     | Controller → Service → Repository (per service)    |
| DTO (Data Transfer Object)     | Structural     | Separate request/response DTOs per layer           |
| Mapper                         | Structural     | `UserMapper` component in `helper/`                |
| Repository                     | Structural     | Spring Data JPA interfaces                         |
| Chain of Responsibility        | Behavioral     | JWT filter chains (Gateway + each service)         |
| Observer (Pub/Sub)             | Behavioral     | Kafka producers and consumers                      |
| Validator                      | Behavioral     | `UserValidator`, `AuthValidator` in `helper/`      |
| Stateless JWT Authentication   | Security       | HMAC-SHA256 JWT with shared secret                 |
| Hybrid RBAC / ABAC             | Security       | `@PreAuthorize` + `SecurityOwnershipService`       |
| Defense in Depth               | Security       | Dual JWT validation (Gateway + downstream)         |
| Global Exception Handler       | Cross-cutting  | `@ControllerAdvice` classes                        |
| Constants Extraction           | Cross-cutting  | `constants/` packages (endpoints, logs, topics)    |
| Observability                  | Cross-cutting  | Prometheus + Grafana + SLF4J                       |
| Containerization               | Infrastructure | Docker Compose with health checks and limits       |
