# Hospital Information System (HIS) - Microservices Architecture

This project is a production-grade backend system designed for the digital transformation of hospital operations. The architecture is built on a distributed, event-driven model to manage patients, medical staff, clinical workflows, and administrative logistics with high fault tolerance and security.

## Core Transformation Features

The system has evolved from a basic patient management tool into a comprehensive Hospital Information System with the following capabilities:

### 1. Data Isolation and Security Infrastructure
Database schema isolation (Schema-per-Service) is enforced across all microservices. This ensures that services cannot directly access each other's data, achieving zero-trust security and independent scalability. A centralized API Gateway handles authentication, route management, and performance monitoring.

### 2. Insurance and Advanced Billing
The billing module includes an insurance integration layer. Invoices are automatically split based on patient insurance policies, allowing for dynamic calculation of co-pays and payer coverage rates.

### 3. Medical Staff Roster and Appointments
Advanced shifts, leave management, and work schedules for medical staff are fully integrated. The appointment booking process performs real-time availability checks against doctor schedules to prevent overlaps.

### 4. Laboratory Information System (LIMS)
A dedicated laboratory service digitizes the workflow from test requests to final reports. Clinical orders issued by doctors are transmitted via Kafka to the lab unit, and results are automatically processed back into patient records upon completion.

### 5. Pharmacy and Inventory Management
An inventory service tracks medical supplies and medications. The system supports automated stock deduction during clinical use and providing alerts for critical stock levels or expiring items.

### 6. Bed and Admission Management
The admission service manages inpatient workflows by tracking ward, room, and bed availability. It supports automated daily billing for room charges and coordinates the transition from outpatient appointments to inpatient admissions.

### 7. Messaging Middleware and Fault Tolerance
Kafka communication is standardized across the ecosystem, with Dead Letter Queue (DLQ) mechanisms in every service to maintain data consistency. This ensures the system can recover from errors and process messages without data loss.

## Technical Architecture

The system is composed of the following microservices, containerized with Docker:

| Service | Responsibility | Port |
| :--- | :--- | :--- |
| **API Gateway** | Entry point, routing, and centralized authentication | 4004 |
| **Auth Service** | Authentication and RBAC (Role-Based Access Control) | 8089 |
| **Patient Service** | Patient records, medical history, and clinical results | 8080 |
| **Doctor Service** | Doctor profiles, clinical ordering, and outbox relay | 8083 |
| **Lab Service** | LIMS workflow, test processing, and result completion | 8087 |
| **Appointment Service** | Scheduling, rostering, and availability management | 8084 |
| **Billing Service** | Invoicing, insurance splitting, and payment tracking | 8081 |
| **Inventory Service** | Medical supplies tracking and stock alerts | 8088 |
| **Admission Service** | Bed management and inpatient ward logistics | 8086 |

## Security and Hardening

The system implements the following technical hardening standards:

* **User-Level Database Isolation**: Each service connects to PostgreSQL using dedicated users with permissions limited to their specific schema.
* **Outbox Pattern**: Reliable event delivery from the Doctor and Lab services using a persistent outbox to prevent data loss during Kafka outages.
* **Transactional Consumption**: Atomic processing of Kafka events and database updates to prevent duplicate data or inconsistent states.
* **Standardized Serialization**: Consistent Java 8 Time serialization using Spring-managed ObjectMappers across all distributed nodes.

## Development and Testing

The system includes a comprehensive End-to-End (E2E) API testing suite that validates the full clinical lifecycle, from administrative user registration through to the propagation of lab results into patient records.

To run the full validation suite:
```bash
cd scripts/tests
mvn test
```

> For detailed descriptions of each microservice, data models, and validation flows, refer to the [release link](https://doguhanniltextra.github.io/patient-management/).
