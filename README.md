

# 🏥 Patient Management System – Microservices Architecture

This project is a **microservices-based backend system** for managing patients, doctors, appointments, and billing in a healthcare environment. It follows a modular and scalable design.

> Detailed service content and models can be found [in this release link](https://doguhanniltextra.github.io/patient-management/).

---

## ⚙️ Architecture

The system is composed of the following microservices, all containerized with Docker:

* **API Gateway**
* **Doctor Service**
* **Patient Service**
* **Appointment Service**
* **Billing Service**
* **Analytics Service**

Services communicate via REST APIs with validation, creation, and synchronization mechanisms.

---

## 🐳 Dockerized Services & Ports

All services run independently in Docker containers. Ports:

| Service             | Port |
| ------------------- | ---- |
| API Gateway         | 4004 |
| Patient Service     | 8080 |
| Doctor Service      | 8083 |
| Appointment Service | 8084 |
| Billing Service     | 8081 |
| Analytics Service   | 8082 |

---

> For detailed descriptions of each microservice, their data models, and validation flows, please refer to [this release link](https://github.com/doguhanniltextra/patient-management/releases/tag/v1.0).

