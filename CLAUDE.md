# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Goal

Backend portfolio project targeting fintech engineering roles. Simulates a payment lifecycle API:

1. **Accept** — REST endpoint receives a transaction request and validates it
2. **Publish** — Valid transaction is published as an event to AWS SQS
3. **Consume** — SQS consumer processes the event and updates transaction status in PostgreSQL

Target stack: Java 21, Spring Boot 3.5, AWS SQS, PostgreSQL, deployed on AWS.

## Build & Run Commands

```bash
./mvnw clean install          # Full build
./mvnw spring-boot:run        # Run the application
./mvnw test                   # Run all tests
./mvnw test -Dtest=ClassName  # Run a single test class
./mvnw spring-boot:build-image # Build OCI Docker image
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Architecture

Event-driven payment processing platform built on Spring Boot 3.5 / Java 21.

**Planned architecture** (packages exist but are empty scaffolds):
- `controller/` — REST API endpoints
- `service/` — Business logic
- `repository/` — Spring Data JPA repositories
- `model/entity/` — JPA entities
- `model/dto/` — Request/response DTOs
- `messaging/` — Event/message handling (AWS integration planned)
- `exception/` — Global exception handlers
- `config/` — Spring configuration classes

## Code Conventions

**Lombok:**
- Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` on entity and DTO classes
- Use `@Builder.Default` whenever a field has a default value in a `@Builder` class
- Use `@RequiredArgsConstructor` on `@Service`, `@Component`, and `@Configuration` classes for constructor injection — never use `@Autowired`

**JPA:**
- Always use `@Enumerated(EnumType.STRING)` for enum fields
- Always set `updatable = false` on `createdAt` timestamp fields
- Always use `GenerationType.UUID` for UUID primary keys
- Always use `@PrePersist` and `@PreUpdate` (private methods) for timestamp management

**Validation:**
- Always add Bean Validation annotations on DTO request objects
- `@NotNull` on required object fields, `@NotBlank` on required String fields, `@DecimalMin("0.01")` on monetary amounts

**General:**
- Package root is `com.bridgepay.payment_processor` (underscore, not camelCase)
- All monetary values use `BigDecimal`, never `double` or `float`

**Key notes:**
- Package root: `com.bridgepay.payment_processor` (underscore, not hyphen)
- `DataSourceAutoConfiguration` is currently excluded in `application.properties` — remove this exclusion once a database is configured
- Testcontainers is set up for integration tests (requires Docker)
- Lombok is on the classpath; use `@Data`, `@Builder`, etc. freely
