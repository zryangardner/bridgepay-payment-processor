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
| Database (production) | PostgreSQL (planned) |
| Messaging | AWS SQS (planned) |
| Cloud | AWS — ECS, RDS, SQS (planned) |
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
     └──▶ SqsPublisher         (Messaging — event publishing) [planned]
               │
               ▼
          AWS SQS Queue
               │
               ▼
          PaymentEventConsumer  (Async processing) [planned]
               │
               ▼
          PaymentRepository     (Status update → PROCESSING)
```

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
  "createdAt": "2026-03-11T15:24:17.320167",
  "updatedAt": "2026-03-11T15:24:17.320167"
}
```

### Payment Status Values

| Status | Description |
|---|---|
| `PENDING` | Payment created, awaiting processing |
| `PROCESSING` | Event consumed, processing in progress |
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
│   └── dto/             # Request/response DTOs
├── messaging/           # SQS producer and consumer [planned]
├── config/              # AWS and Spring configuration [planned]
└── exception/           # Custom exceptions and global handler
```

---

## Running Locally

### Prerequisites

- Java 21
- Maven

### Start the application

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

### H2 Console

An in-memory H2 database is used for local development. Access the console at:

```
http://localhost:8080/h2-console
```

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:bridgepay` |
| Username | `sa` |
| Password | *(empty)* |

---

## Running Tests

```bash
./mvnw test
```

### Test Coverage

| Layer | Class | Type |
|---|---|---|
| Service | `PaymentServiceTest` | Unit — Mockito |
| Controller | `PaymentControllerTest` | `@WebMvcTest` |
| Repository | `PaymentRepositoryTest` | `@DataJpaTest` / H2 |

All 15 tests passing.

---

## Roadmap

### In Progress
- [ ] AWS SQS integration — publish `PaymentCreatedEvent` on payment creation
- [ ] `PaymentEventConsumer` — async SQS consumer to update status to `PROCESSING`
- [ ] AWS SQS configuration class with `SqsClient` bean

### Planned
- [ ] Swap H2 for PostgreSQL with Spring profiles (`local` vs `prod`)
- [ ] AWS RDS PostgreSQL instance
- [ ] Dockerize the application
- [ ] Deploy to AWS ECS (Fargate)
- [ ] GitHub Actions CI/CD pipeline — build, test, deploy on push to `main`
- [ ] Architecture diagram
- [ ] Postman collection for API testing
- [ ] Integration tests with Testcontainers (PostgreSQL + LocalStack for SQS)

---

## Related Projects

| Repo | Stack | Description |
|---|---|---|
| `bridgepay-notification-service` | Kotlin / Spring Boot | Lifecycle notification dispatcher consuming SQS events *(coming soon)* |
| `bridgepay-insights-api` | Python / FastAPI | AI-powered payment insights API *(coming soon)* |

---

## Author

Zachary Gardner — [LinkedIn](https://linkedin.com/in/zryangardner) · [GitHub](https://github.com/zryangardner)