# 🏨 PG Finder — Microservices-Based PG & Hostel Reservation System

<div align="center">

### A scalable, event-driven backend for PG/hostel discovery, bed inventory, authentication, and reservations.

![Java](https://img.shields.io/badge/Java-17%20%7C%2021-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.x-6DB33F?style=for-the-badge\&logo=spring\&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-4.x-231F20?style=for-the-badge\&logo=apachekafka\&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge\&logo=mysql\&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-JJWT-000000?style=for-the-badge\&logo=jsonwebtokens\&logoColor=white)

</div>

---

## 📌 Overview

**PG Finder** is a microservices-based application designed to manage **Paying Guest (PG) and hostel properties, rooms, beds, users, and reservations**.

The system is built using **Spring Boot and Spring Cloud** and follows a distributed architecture where each major business domain is implemented as an independent microservice.

The project demonstrates practical implementation of:

* Microservices Architecture
* REST APIs
* API Gateway
* Service Discovery
* JWT Authentication
* Role-Based Access Control
* OpenFeign communication
* Apache Kafka event streaming
* Database-per-Service architecture
* Spring Data JPA
* MySQL
* Asynchronous notification processing

---

# 🏗️ System Architecture

```mermaid
flowchart TD

    Client["🌐 Client<br/>React / Postman"] --> Gateway["🚪 API Gateway<br/>Port: 9090"]

    Gateway --> Eureka["🔍 Eureka Server<br/>Port: 8761"]

    Gateway --> User["👤 User Service<br/>Port: 9191"]
    Gateway --> Hostel["🏢 Hostel Service<br/>Port: 9192"]
    Gateway --> Booking["📅 Booking Service<br/>Port: 9193"]

    User --> UserDB[("MySQL<br/>user_db")]
    Hostel --> HostelDB[("MySQL<br/>hostel_db")]
    Booking --> BookingDB[("MySQL<br/>booking_db")]

    Booking -->|OpenFeign| Hostel
    Booking -->|OpenFeign| User

    Booking -->|Publish Booking Event| Kafka["⚡ Apache Kafka<br/>booking-events"]

    Kafka -->|Consume Event| Notification["📩 Notification Service<br/>Port: 9194"]
```

---

# 🧩 Microservices

| Service              |   Port | Technology                         | Database     | Responsibility                             |
| -------------------- | -----: | ---------------------------------- | ------------ | ------------------------------------------ |
| Eureka Server        | `8761` | Spring Cloud Eureka                | —            | Service discovery and registration         |
| API Gateway          | `9090` | Spring Cloud Gateway               | —            | Routing, authentication filtering and CORS |
| User Service         | `9191` | Spring Boot, Spring Security, JJWT | `user_db`    | Users, authentication and authorization    |
| Hostel Service       | `9192` | Spring Boot, Spring Data JPA       | `hostel_db`  | Hostels, rooms and beds                    |
| Booking Service      | `9193` | Spring Boot, OpenFeign, Kafka      | `booking_db` | Reservations and booking lifecycle         |
| Notification Service | `9194` | Spring Boot, Spring Kafka          | —            | Asynchronous event processing              |
| Kafka Broker         | `9092` | Apache Kafka                       | —            | Event streaming                            |

---

# 🔐 Authentication & Authorization

PG Finder uses **JWT-based authentication**.

### Authentication Flow

```text
User
 │
 │ Login
 ▼
User Service
 │
 │ Validate credentials
 │
 │ Generate JWT
 ▼
JWT Token
 │
 │ Subsequent requests
 ▼
API Gateway
 │
 │ Validate JWT
 ▼
Downstream Microservice
```

Passwords are securely stored using **BCrypt hashing**.

The application supports three roles:

```text
USER
OWNER
ADMIN
```

### Example

| Role    | Example Responsibility                        |
| ------- | --------------------------------------------- |
| `USER`  | Search PGs and create/manage bookings         |
| `OWNER` | Manage PG/hostel properties                   |
| `ADMIN` | Administrative operations and user management |

---

# 🚪 API Gateway

The **API Gateway** acts as the single entry point for client requests.

Responsibilities include:

* Request routing
* JWT validation
* CORS configuration
* Authentication filtering
* Forwarding requests to appropriate microservices
* Service discovery integration

Example:

```text
Client
   |
   | GET /api/v1/hostels
   ▼
API Gateway :9090
   |
   | Discover hostel-service through Eureka
   ▼
Hostel Service :9192
```

Clients do not need to directly communicate with individual microservice ports.

---

# 🔍 Service Discovery with Eureka

The project uses **Netflix Eureka Server** for service registration and discovery.

Each microservice registers itself with Eureka.

```text
              Eureka Server
                 :8761
                    |
       +------------+------------+
       |            |            |
       ▼            ▼            ▼
 user-service  hostel-service  booking-service
```

Instead of relying on fixed service URLs, services can communicate using registered service names.

For example:

```text
lb://USER-SERVICE
lb://HOSTEL-SERVICE
lb://BOOKING-SERVICE
```

This makes the architecture more suitable for running multiple instances of a service.

---

# 🔄 Inter-Service Communication

PG Finder uses **OpenFeign** for synchronous communication between services.

### Booking → Hostel

When creating a booking, the Booking Service can communicate with the Hostel Service to retrieve or verify relevant hostel/room/bed information.

```text
Booking Service
       |
       | OpenFeign
       ▼
Hostel Service
       |
       ▼
Room / Bed Information
```

### Booking → User

The Booking Service can also communicate with the User Service when user information is required.

```text
Booking Service
       |
       | OpenFeign
       ▼
User Service
```

---

# ⚡ Event-Driven Architecture with Kafka

Apache Kafka is used for **asynchronous event processing**.

The Booking Service acts as a Kafka producer, while the Notification Service acts as a Kafka consumer.

```text
Booking Service
      |
      | Publish
      ▼
 booking-events
      |
      | Consume
      ▼
Notification Service
```

### Booking Event

A booking event can contain information such as:

```text
Booking ID
User ID
Hostel / Bed information
Booking status
Timestamp
```

Booking lifecycle states include:

```text
PENDING
CONFIRMED
CANCELLED
```

The notification process is separated from the booking request so that notification handling does not need to be tightly coupled with the booking transaction.

---

# 🗄️ Database-per-Service

Each business microservice owns its own database/schema.

```text
User Service
     │
     └── user_db

Hostel Service
     │
     └── hostel_db

Booking Service
     │
     └── booking_db
```

This follows the **Database-per-Service** pattern.

### Advantages

* Service-level data ownership
* Reduced database coupling
* Independent schema evolution
* Better service isolation
* Independent deployment possibilities

A service should not directly access another service's database.

Instead:

```text
❌ Booking Service → hostel_db

✅ Booking Service → Hostel Service API
```

---

# 📡 API Endpoints

All client requests are intended to go through:

```text
http://localhost:9090
```

## 🔐 Authentication & Users

| Method | Endpoint                | Access        | Description                 |
| ------ | ----------------------- | ------------- | --------------------------- |
| `POST` | `/api/v1/auth/register` | Public        | Register a user             |
| `POST` | `/api/v1/auth/login`    | Public        | Authenticate and obtain JWT |
| `GET`  | `/api/v1/users/me`      | Authenticated | Get current user profile    |
| `GET`  | `/api/v1/users`         | ADMIN         | Get registered users        |

---

## 🏢 Hostels & Inventory

| Method | Endpoint               | Access        | Description           |
| ------ | ---------------------- | ------------- | --------------------- |
| `GET`  | `/api/v1/hostels`      | Authenticated | Get available hostels |
| `GET`  | `/api/v1/hostels/{id}` | Authenticated | Get hostel details    |
| `POST` | `/api/v1/hostels`      | OWNER / ADMIN | Create hostel         |
| `GET`  | `/api/v1/rooms`        | Authenticated | Get rooms             |
| `GET`  | `/api/v1/beds`         | Authenticated | Get bed information   |

---

## 📅 Bookings

| Method  | Endpoint                        | Access        | Description         |
| ------- | ------------------------------- | ------------- | ------------------- |
| `POST`  | `/api/v1/bookings`              | Authenticated | Create a booking    |
| `GET`   | `/api/v1/bookings`              | Authenticated | Get booking history |
| `PATCH` | `/api/v1/bookings/{id}/confirm` | OWNER / ADMIN | Confirm booking     |
| `PATCH` | `/api/v1/bookings/{id}/cancel`  | Authenticated | Cancel booking      |

---

# 🔁 Booking Workflow

A typical booking flow is:

```text
1. User logs in
       ↓
2. User receives JWT
       ↓
3. User searches available hostels
       ↓
4. User selects a room/bed
       ↓
5. User sends booking request
       ↓
6. API Gateway validates JWT
       ↓
7. Booking Service receives request
       ↓
8. Booking Service communicates with required services
       ↓
9. Booking is created
       ↓
10. Booking event is published to Kafka
       ↓
11. Notification Service consumes event
       ↓
12. Notification is processed asynchronously
```

---

# 🛠️ Technologies Used

### Backend

* Java
* Spring Boot
* Spring MVC / REST
* Spring Data JPA
* Spring Security
* Spring Cloud
* Spring Cloud Gateway
* Spring Cloud OpenFeign
* Netflix Eureka
* Spring Kafka
* JJWT
* Lombok

### Database

* MySQL 8.0

### Messaging

* Apache Kafka
* Kafka KRaft mode

### Development Tools

* Maven
* Git
* GitHub
* Postman
* IntelliJ IDEA / Eclipse / VS Code

---

# 📂 Project Structure

A typical repository structure is:

```text
PG-Finder/
│
├── eureka-server/
│
├── api-gateway/
│
├── user-service/
│
├── hostel-service/
│
├── booking-service/
│
├── notification-service/
│
└── README.md
```

Each microservice follows a layered structure such as:

```text
src/
└── main/
    ├── java/
    │   └── com.pgfinder/
    │       ├── controller/
    │       ├── service/
    │       ├── repository/
    │       ├── entity/
    │       ├── dto/
    │       ├── exception/
    │       └── config/
    │
    └── resources/
        └── application.properties
```

---

# ⚙️ Local Setup Guide

## Prerequisites

Install the following:

* JDK 17 or 21
* Maven 3.8+
* MySQL 8.0+
* Apache Kafka 4.x
* Git
* Postman

---

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/venky4378/PG-Finder.git
```

```bash
cd PG-Finder
```

> Update the repository URL above if your actual PG Finder repository uses a different GitHub name.

---

## 2️⃣ Create MySQL Databases

Open MySQL Workbench or MySQL CLI and execute:

```sql
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS hostel_db;
CREATE DATABASE IF NOT EXISTS booking_db;
```

Configure the database credentials in the corresponding microservice configuration files.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/user_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

# 3️⃣ Start Kafka

PG Finder uses Apache Kafka in KRaft mode.

Example Windows setup:

```powershell
$env:KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"
```

Format the Kafka storage directory if required:

```powershell
.\bin\windows\kafka-storage.bat format -t YOUR_CLUSTER_ID -c .\config\server.properties --standalone
```

Start Kafka:

```powershell
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

Kafka should be available on:

```text
localhost:9092
```

---

# 4️⃣ Start Microservices

Start the services in the following order:

```text
1. Eureka Server       → 8761
2. User Service        → 9191
3. Hostel Service      → 9192
4. Booking Service     → 9193
5. Notification Service → 9194
6. API Gateway         → 9090
```

Eureka Dashboard:

```text
http://localhost:8761
```

API Gateway:

```text
http://localhost:9090
```

---

# 🧪 Testing

You can test the APIs using **Postman**.

Recommended flow:

```text
Register
   ↓
Login
   ↓
Copy JWT
   ↓
Send JWT in Authorization header
   ↓
Access protected APIs
   ↓
Create / manage booking
   ↓
Verify Kafka event
   ↓
Verify Notification Service consumption
```

Authorization header:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

# 🔒 Security

The application implements:

* JWT authentication
* BCrypt password hashing
* Role-based authorization
* Gateway-level token validation
* Protected REST endpoints
* Authenticated user context propagation

Example:

```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

The API Gateway validates the token before forwarding secured requests.

---

# 📐 Design Patterns & Architecture Concepts

The project demonstrates several real-world distributed-system concepts:

### API Gateway Pattern

Central entry point for client requests.

### Database-per-Service Pattern

Each business service owns its own database.

### Service Discovery Pattern

Eureka dynamically tracks service instances.

### Event-Driven Architecture

Kafka decouples booking events from notification processing.

### Synchronous Communication

OpenFeign is used when an immediate response from another service is required.

### Layered Architecture

Controllers, services, repositories, DTOs, entities, and configuration are separated by responsibility.

---

# 🚧 Current Roadmap

* [x] Spring Boot Microservices
* [x] Eureka Service Discovery
* [x] API Gateway
* [x] JWT Authentication
* [x] Role-Based Access Control
* [x] Database-per-Service
* [x] OpenFeign Communication
* [x] Apache Kafka Integration
* [x] Notification Service
* [ ] React Frontend
* [ ] Resilience4j Circuit Breaker
* [ ] Docker & Docker Compose
* [ ] Centralized Configuration
* [ ] Distributed Tracing
* [ ] AWS Deployment
* [ ] Kubernetes Deployment
* [ ] CI/CD Pipeline

---

# 🎯 Key Learning Outcomes

Through this project, I gained practical experience with:

* Designing microservices around business domains
* Building RESTful APIs using Spring Boot
* Implementing JWT authentication and RBAC
* Using API Gateway as a centralized entry point
* Registering and discovering services with Eureka
* Implementing synchronous communication using OpenFeign
* Implementing asynchronous communication using Kafka
* Designing independent service databases
* Handling distributed service communication
* Structuring a scalable backend application

---

# 👨‍💻 Author

### Venkayya Swamy Chamanthi

**Java Full Stack Developer**

GitHub: [@venky4378](https://github.com/venky4378)

LinkedIn: [Swamy Ch](https://www.linkedin.com/in/swamy-ch/)

Email: **[venkyswamy437@gmail.com](mailto:venkyswamy437@gmail.com)**

---

## ⭐ If you find this project useful

Feel free to explore the repository, raise issues, or suggest improvements.

**Built with Java + Spring Boot + Spring Cloud + Kafka + MySQL**
