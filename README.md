# Banking System - Spring Boot

Production-style banking backend built using Java 17 and Spring Boot.

## Tech Stack

Java 17 | Spring Boot | Spring Security | JWT | JPA | H2 | Kafka | Docker | Swagger | JUnit

## Features

- Customer & User Management
- Account Management
- Banking Transactions
- JWT Authentication & Authorization
- Kafka Producer/Consumer
- Validation & Exception Handling
- Swagger/OpenAPI
- Docker Containerization
- Unit Testing

## Architecture

Client
  ↓
REST API
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
Database
  │
  └── Transaction Event → Kafka → Consumer

## Run Locally


mvn clean package

mvn test

mvn spring-boot:run   

Application: http://localhost:8080  

Swagger
http://localhost:8080/swagger-ui/index.html

## Docker

mvn clean package -DskipTests

docker build -t banking-system:1.0 .

ocker run -d --name banking-system-container -p 8080:8080 banking-system:1.0

## Git Workflow

Feature Branch → Test → Commit → Push → Pull Request → Merge

## Author

**Prashant Mishra**
