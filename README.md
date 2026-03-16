# BridgePay Payment Processor

> Event-driven payment processing platform built on Java Spring Boot and AWS.

A portfolio project simulating a real-world payment lifecycle API — accepting transactions, validating them, publishing events to AWS SQS, and processing them asynchronously. Built to demonstrate backend engineering skills in Java, Spring Boot, event-driven architecture, and AWS cloud infrastructure.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.11 |
| Persistence | Spring Data JPA / Hibernate 6 |
| Database (local) | H2 (in-memory) |
| Database (production) | PostgreSQL — AWS RDS |
| Messaging | AWS SQS |
| Cloud | AWS — ECS Fargate, RDS, SQS, ECR, ALB |
| Containerization | Docker |
| Build | Maven |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers |

---

## Architecture
```
HTTP Request
     │
     ▼
PaymentController          (REST layer — /api/v1/payments)
     │
     ▼
PaymentService             (Business logic — validation, mapping)
     │
     ├──▶ PaymentRepository    (Spring Data JPA — persistence)
     │
     └──▶ SqsPublisher         (Messaging — event publishing)
               │
               ▼
          AWS SQS Queue
               │
               ▼
          PaymentEventConsumer  (Async processing)
               │
               ▼
          PaymentRepository     (Status update → PROCESSING)
```

---

## AWS Infrastructure

| Component | Service | Details |
|---|---|---|
| Container Registry | Amazon ECR | Stores Docker image |
| Container Host | Amazon ECS Fargate | Runs containerized Spring Boot app |
| Database | Amazon RDS PostgreSQL | Production persistence |
| Messaging | Amazon SQS | Async payment event queue |
| Load Balancer | Application Load Balancer | Public-facing HTTP endpoint |

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/payments` | Create a new payment |
| `GET` | `/api/v1/payments/{id}` | Get payment by ID |
| `GET` | `/api/v1/payments/status/{status}` | Get payments by status |
| `GET` | `/api/v1/payments/sender/{senderId}` | Get payments by sender |
| `PATCH` | `/api/v1/payments/{id}/status` | Update payment status |

### Create Payment — Example Request
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "currency": "USD",
    "senderId": "user-001",
    "recipientId": "user-002",
    "description": "Payment for services"
  }'
```

### Example Response
```json
{
  "id": "f1f08280-bd82-4951-8076-8ae7969c4b95",
  "amount": 100.00,
  "currency": "USD",
  "status": "PENDING",
  "senderId": "user-001",
  "recipientId": "user-002",
  "description": "Payment for services",
  "createdAt": "2026-03-12T15:41:43.974837",
  "updatedAt": "2026-03-12T15:41:43.974837"
}
```

### Error Responses

Invalid input returns a `400 Bad Request` with a descriptive message. Examples:

| Scenario | Response |
|---|---|
| Invalid UUID on `/{id}` | `400` — expected a valid UUID |
| Invalid status on `/status/{status}` | `400` — expected one of [PENDING, PROCESSING, COMPLETED, FAILED] |
| Whitespace-only `senderId` | `400` — senderId must not be blank |

---

### Payment Status Values

| Status | Description |
|---|---|
| `PENDING` | Payment created, awaiting processing |
| `PROCESSING` | SQS event consumed, processing in progress |
| `COMPLETED` | Payment successfully processed |
| `FAILED` | Payment processing failed |

---

## Project Structure
```
src/main/java/com/bridgepay/payment_processor/
├── controller/          # REST endpoints
├── service/             # Business logic
├── repository/          # Spring Data JPA repositories
├── model/
│   ├── entity/          # JPA entities and enums
│   └── dto/             # Request/response DTOs (PaymentRequest, PaymentResponse, PaymentCreatedEvent)
├── messaging/           # SqsPublisher, PaymentEventConsumer
├── config/              # SqsConfig — SqsTemplate bean configuration
└── exception/           # GlobalExceptionHandler, custom exceptions

src/main/resources/
├── application.properties          # Shared config — app name, JPA, AWS region, SQS queue URL
├── application-local.properties    # Local dev — H2 in-memory database
└── application-prod.properties     # Production — RDS PostgreSQL via environment variables
```

---

## Running Locally

### Prerequisites

- Java 21
- Maven
- Docker (optional, for containerized local run)

### Start the application (local profile)
```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` using H2 in-memory database.

### H2 Console
```
http://localhost:8080/h2-console
```

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:bridgepay` |
| Username | `sa` |
| Password | *(empty)* |

### Start with production profile

Set the following environment variables, then run:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

| Variable | Description |
|---|---|
| `DB_URL` | RDS PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `AWS_ACCESS_KEY_ID` | AWS credentials |
| `AWS_SECRET_ACCESS_KEY` | AWS credentials |
| `SQS_QUEUE_URL` | Full SQS queue URL |

---

## Docker

### Build the image
```bash
docker build -t bridgepay-payment-processor .
```

### Run the container
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=<your-rds-url> \
  -e DB_USERNAME=<username> \
  -e DB_PASSWORD=<password> \
  -e AWS_ACCESS_KEY_ID=<key> \
  -e AWS_SECRET_ACCESS_KEY=<secret> \
  -e SQS_QUEUE_URL=<queue-url> \
  bridgepay-payment-processor
```

---

## Running Tests
```bash
./mvnw test
```

### Test Coverage

| Layer | Class | Tests | Type |
|---|---|---|---|
| Service | `PaymentServiceTest` | Core CRUD + business logic | Unit — Mockito |
| Controller | `PaymentControllerTest` | Happy path + input validation (invalid UUID, invalid enum, blank senderId) | `@WebMvcTest` |
| Repository | `PaymentRepositoryTest` | Persistence queries | `@DataJpaTest` / H2 |
| Messaging | `SqsPublisherTest` | Event publishing | Unit — Mockito |
| Messaging | `PaymentEventConsumerTest` | Async status updates | Unit — Mockito |

All 27 tests passing.

---

## Roadmap

### Completed
- [x] Full layered REST API — Controller, Service, Repository
- [x] JPA entity with UUID primary key, Bean Validation, custom exception handling
- [x] Input validation — `@Validated` on controller, `@NotBlank` on path variables, type-aware error responses for invalid UUIDs and enums
- [x] Spring profile configuration — local (H2) and prod (RDS PostgreSQL)
- [x] AWS RDS PostgreSQL — production database
- [x] AWS SQS integration — event publishing and async consumption
- [x] Full PENDING → PROCESSING lifecycle verified end-to-end
- [x] Dockerized application
- [x] Deployed to AWS ECS Fargate with Application Load Balancer
- [x] Postman collection for API testing

### Planned
- [ ] GitHub Actions CI/CD pipeline — build, test, deploy on push to `main`
- [ ] Architecture diagram
- [ ] Integration tests with Testcontainers (PostgreSQL + LocalStack for SQS)
- [ ] Dead letter queue (DLQ) for failed SQS messages

---

## Related Projects

| Repo | Stack | Description |
|---|---|---|
| `bridgepay-notification-service` | Kotlin / Spring Boot | Lifecycle notification dispatcher consuming SQS events *(coming soon)* |
| `bridgepay-insights-api` | Python / FastAPI | AI-powered payment insights API *(coming soon)* |
| `bridgepay-dashboard` | React | Payment status dashboard and registration UI *(coming soon)* |

---

## Author

Zachary Gardner — [LinkedIn](https://linkedin.com/in/zryangardner) · [GitHub](https://github.com/zryangardner)