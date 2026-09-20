# 🏨 PG Finder – Event-Driven Microservices Platform
[![Java](https://img.shields.io/badge/Java-17%2F21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-4.x%20(KRaft)-black.svg)](https://kafka.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-JJWT%200.12.6-red.svg)](https://jwt.io/)
A distributed, event-driven accommodation reservation platform designed to streamline hostel/PG discovery, bed reservations, role-based access management, and asynchronous customer notifications.
---
## 🏛️ System Architecture
```mermaid
flowchart TD
    Client["Client / React Frontend / Postman"] -->|HTTP / REST| Gateway["API Gateway (:9090)<br>JWT Gatekeeper & Rate Limiter"]
    
    Gateway -->|Service Discovery| Eureka["Eureka Service Registry (:8761)"]
    
    Gateway -->|X-User-Id, X-User-Role| UserService["User Service (:9191)<br>Auth & RBAC (user_db)"]
    Gateway -->|Routing| HostelService["Hostel Service (:9192)<br>Hostels, Rooms, Beds (hostel_db)"]
    Gateway -->|Routing| BookingService["Booking Service (:9193)<br>Reservations (booking_db)"]
    
    BookingService -.->|OpenFeign Lookup| HostelService
    BookingService -.->|OpenFeign Lookup| UserService
    
    BookingService -->|Publish BookingEvent| Kafka["Apache Kafka Broker (:9092)<br>Topic: booking-events"]
    Kafka -->|Consume Event| NotificationService["Notification Service (:9194)<br>SMS, WhatsApp & Email Dispatcher"]
🚀 Key Features
Centralized API Gateway & Gatekeeper Pattern: Single entrypoint (:9090) with a custom Reactive GlobalFilter that validates HMAC-SHA JWT tokens and injects claims (X-User-Id, X-User-Role) downstream.
Role-Based Access Control (RBAC): Fine-grained security for USER, OWNER, and ADMIN roles, preventing unauthorized access to restricted endpoints.
Event-Driven Messaging via Apache Kafka: Booking lifecycle updates are asynchronously published to Kafka (in modern KRaft mode, no ZooKeeper required), eliminating latency in booking transactions.
Service Discovery & Dynamic Load Balancing: Zero hardcoded IP addresses using Netflix Eureka and Spring Cloud client-side load balancing.
Synchronous Inter-Service Communication: Seamless cross-service verification via Spring Cloud OpenFeign.
Database-per-Service Isolation: Independent MySQL schemas (user_db, hostel_db, booking_db) ensuring microservice autonomy and high cohesion.
📦 Microservices Inventory
Microservice	Port	Database	Primary Responsibility
Eureka Server	8761	—	Service discovery and heartbeats
API Gateway	9090	—	Routing, load balancing, JWT gatekeeper, CORS
User Service	9191	user_db	User registration, BCrypt authentication, JWT issuance, RBAC
Hostel Service	9192	hostel_db	PG hostels, rooms, bed inventory and status management
Booking Service	9193	booking_db	Booking lifecycle, Kafka event producer
Notification Service	9194	—	Event-driven Kafka consumer, simulates SMS/Email alerts
📡 Core API Endpoints
All external requests are routed through API Gateway (http://localhost:9090):

1. Authentication & Users
POST /api/v1/auth/register — Register a new USER or OWNER (Public)
POST /api/v1/auth/login — Authenticate and receive JWT Bearer token (Public)
GET /api/v1/users/me — Fetch current user profile (Requires JWT)
GET /api/v1/users — Fetch directory of all users (ADMIN only)
2. Hostel & Inventory
GET /api/v1/hostels — List all hostels (Requires JWT)
GET /api/v1/hostels/{id} — Get hostel details with room/bed availability (Requires JWT)
POST /api/v1/hostels — Register a new hostel (OWNER / ADMIN)
GET /api/v1/rooms & GET /api/v1/beds — Inspect room and bed statuses
3. Bookings & Events
POST /api/v1/bookings — Reserve a bed (Triggers Kafka event)
GET /api/v1/bookings — View all bookings
PATCH /api/v1/bookings/{id}/confirm — Confirm booking (OWNER / ADMIN)
PATCH /api/v1/bookings/{id}/cancel — Cancel booking (Triggers bed release)
🛠️ Tech Stack
Language: Java 17 / 21
Framework: Spring Boot 3.x, Spring Cloud 2023.x (Gateway WebFlux, Eureka, OpenFeign)
Security: JJWT (Java JWT 0.12.6), Spring Security, BCrypt
Event Broker: Apache Kafka 4.x (KRaft Mode)
Database & ORM: MySQL 8.0, Spring Data JPA / Hibernate
Build Tool: Apache Maven
Testing & Tools: Postman, Git, Lombok
⚙️ Local Setup & Execution Guide
Prerequisites
Java JDK 17 or 21 installed.
MySQL 8.0 installed and running on port 3306.
Apache Kafka installed.
1. Database Setup
Create the required MySQL schemas:

sql


CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS hostel_db;
CREATE DATABASE IF NOT EXISTS booking_db;
2. Start Apache Kafka (KRaft Mode)
In your Kafka directory:

bash


# Set heap options to avoid wmic issues on Windows
$env:KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"
# Start the Kafka broker
.\bin\windows\kafka-server-start.bat .\config\server.properties
3. Start Microservices in Order
Run each service using your IDE or terminal:

eureka-server (http://localhost:8761) — Ensure dashboard loads.
user-service (http://localhost:9191)
hostel-service (http://localhost:9192)
booking-service (http://localhost:9193)
notification-service (http://localhost:9194)
api-gateway (http://localhost:9090)
🔮 Upcoming Roadmap
 Service Discovery with Eureka
 Centralized API Gateway JWT Gatekeeper
 Role-Based Access Control (RBAC)
 Asynchronous Kafka Notification Pipeline
 React Frontend (pg-finder-ui) for end-to-end user booking experience
 Resilience4j Circuit Breakers & Fallbacks on inter-service Feign calls
 Docker & Docker Compose for automated multi-container deployment
👨‍💻 Author
Venkayya Swamy Chamanthi

GitHub: @venky4378
LinkedIn: swamy-ch
Email: 
venkyswamy437@gmail.com
