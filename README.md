# 🏥 Patient Management System – Microservices Architecture

This project is a **microservices-based backend system** for managing appointments, patients, doctors, and billing in a healthcare environment. It follows a modular, scalable design and uses different databases tailored to each service's needs.

## ⚙️ Architecture Overview

The system is composed of the following microservices, each containerized via Docker:

* **API Gateway** (Port: 4004)
* **Doctor Service** (Port: 8083)
* **Patient Service** (Port: 8080)
* **Appointment Service** (Port: 8084)
* **Billing Service** (Port: 8081)
* **Analytics Service** (Port: 8082)

All services communicate through REST APIs, with validation, creation, and synchronization mechanisms.

---

## 🧩 Microservices & Technologies

### 🩺 Patient Service

* **Database**: MySQL
* **Responsibilities**: Managing patient records, validations.
* **Model**:

  ```json
  {
    "id": Long,
    "name": String,
    "email": String,
    "phone": String,
    "registrationDate": Date
  }
  ```

### 👨‍⚕️ Doctor Service

* **Database**: MySQL
* **Responsibilities**: Managing doctors and their specialties.
* **Model**:

  ```json
  {
    "id": Long,
    "name": String,
    "specialization": ENUM,
    "phoneNumber": String,
    "email": String,
    "available": Boolean,
    "gender": ENUM
  }
  ```

### 📅 Appointment Service

* **Database**: PostgreSQL

* **Responsibilities**: Creating appointments, verifying IDs, notifying other services.

* **Model**:

  ```json
  {
    "patientId": Long,
    "doctorId": Long,
    "date": Date,
    "time": String,
    "serviceType": String
  }
  ```

* **Validation Flow**:

    * On appointment creation, the system sends GET requests to:

        * `Patient Service`: to verify if the `patientId` exists.
        * `Doctor Service`: to verify if the `doctorId` exists.
    * If both are valid, the appointment is created.

### 💰 Billing Service

* **Database**: PostgreSQL
* **Responsibilities**: Creating billing records after appointments.
* **Receives Data**:

  ```json
  {
    "patientId": Long,
    "appointmentId": Long,
    "paymentStatus": Boolean,
    "paymentType": ENUM
  }
  ```
### 🔊 Kafka Integration

* Events are published to topics like `appointment-events`, `billing-events`, etc.
* Example flow:

    * `AppointmentCreated` → triggers listeners in `Billing Service` and `Analytics Service`
* Improves scalability and loose coupling between services.


### 📊 Analytics Service

* **Database**: MongoDB
* **Responsibilities**: Storing event-based and analytical data (e.g., trends, usage stats).

---

## 🛢 Why These Databases?

* **PostgreSQL**: Used in appointment and billing services for strong ACID compliance and relational integrity.
* **MySQL**: Used in doctor and patient services for well-supported relational storage with easier scaling.
* **MongoDB**: Used in analytics for flexibility, performance on read-heavy operations, and support for semi-structured data.

---

## 🐳 Dockerized Services

All microservices are containerized and orchestrated via Docker. Each service runs independently and communicates through internal ports.

* API Gateway maps requests to the relevant services.
* Services publish or listen for events and communicate with databases.

**Docker Components**:

```bash
- api-gateway
- patient-service
- doctor-service
- appointment-service
- billing-service
- analytics-service
- postgres
- mysql
- mongodb
```

---

## ✅ Validation Logic for Appointments

When a user tries to create an appointment:

1. The **Appointment Service** receives the request.
2. It sends GET requests to both:

    * **Patient Service** → "is patient ID valid?"
    * **Doctor Service** → "is doctor ID valid?"
3. If both validations pass, the appointment is created.
4. A billing record is triggered.

---

## 📡 Port Mapping Summary

| Service             | Port |
| ------------------- | ---- |
| API Gateway         | 4004 |
| Patient Service     | 8080 |
| Doctor Service      | 8083 |
| Appointment Service | 8084 |
| Billing Service     | 8081 |
| Analytics Service   | 8082 |

