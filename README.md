# Weather Subscription API

[![CI](https://github.com/OWNER/weather-subscription-api/actions/workflows/ci.yml/badge.svg)](https://github.com/OWNER/weather-subscription-api/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/coverage-0%25-lightgrey)](https://github.com/OWNER/weather-subscription-api/actions/workflows/ci.yml)

A compact Spring Boot service that lets users **subscribe to weather updates by city** and receive email notifications.
Tech highlights: REST API, validation, JPA + Flyway, async mail sending, scheduled jobs, external HTTP client, Docker.

## Features
- Subscribe with **email + city** (unique pair).
- **List** subscriptions with pagination (`page`, `size` with guard `1..100`).
- **Delete** a subscription by id.
- Hourly (configurable) **scheduled job** that:
  - Fetches current temperature per city (demo public API via `RestTemplate`).
  - Sends emails **asynchronously** using `@Async` and a pooled `TaskExecutor`.
- Errors are mapped to clean JSON via `@RestControllerAdvice`.

## Tech Stack
- Java 21, Spring Boot 3 (Web, Validation, Data JPA, Mail, Scheduling, Async)
- PostgreSQL + **Flyway** migrations
- Testing: JUnit 5, Mockito
- Docker & Docker Compose

## Project Layout
```
weather-subscription-api/
  src/main/java/com/example/weather
    ├── controller/SubscriptionController.java
    ├── service/{SubscriptionService, NotificationService}.java
    ├── repository/SubscriptionRepository.java
    ├── entity/Subscription.java
    ├── dto/{SubscriptionDto, ErrorResponse}.java
    ├── dto/request/SubscriptionRequest.java
    ├── weather/{WeatherClient, WeatherScheduler}.java
    └── config/{AsyncConfig, WeatherConfig}.java
  src/main/resources
    ├── application.properties
    ├── application-dev.properties
    └── db/migration/V{1,2,3}__*.sql
  Dockerfile
  docker-compose.yml
  pom.xml
```

## API
Base path: `/api/subscriptions`

### Create subscription
`POST /api/subscriptions`  
Request body:
```json
{ "email": "user@example.com", "city": "Kyiv" }
```
Responses:
- `201 Created` — returns created DTO `{ "id": 1, "email": "...", "city": "..." }`
- `400 Bad Request` — validation errors
- `409 Conflict` — duplicate (unique constraint on `email+city`)

### List subscriptions
`GET /api/subscriptions?page=0&size=20`  
- `size` allowed range: **1..100**
- Response: Spring `Page` of DTOs

### Delete subscription
`DELETE /api/subscriptions/{id}`  
- `204 No Content` on success
- `404 Not Found` if id doesn’t exist

## Configuration
`src/main/resources/application.properties` exposes env-driven settings. Key ones:

**Database**
```
SPRING_DATASOURCE_URL     (default jdbc:postgresql://localhost:5432/weather)
SPRING_DATASOURCE_USERNAME (default postgres)
SPRING_DATASOURCE_PASSWORD (default postgres)
```

**Mail**
```
SPRING_MAIL_HOST (default localhost)
SPRING_MAIL_PORT (default 1025)
SPRING_MAIL_USERNAME
SPRING_MAIL_PASSWORD
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH (default false)
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE (default false)
APP_MAIL_FROM (default no-reply@example.com)
```

## Run Locally (no Docker)
```bash
./mvnw spring-boot:run
```
Profiles:
- `default` — Postgres on localhost:5432, Flyway enabled
- `dev` — H2 in-memory + MailHog-style SMTP (defaults to `localhost`; override with `SPRING_MAIL_HOST` if needed)

Use:  
```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

## Run with Docker Compose (recommended)
```bash
docker compose up --build
# app     → http://localhost:8080
# db      → postgres:5432 (user: postgres / pass: postgres / db: weather)
# mailhog → http://localhost:8025 (SMTP: 1025)
```
Compose wires JDBC URL to the containerized Postgres and SMTP to the bundled MailHog service automatically.

## Testing
```bash
./mvnw -q test
```
- Unit tests cover controller/service/scheduler/client.
- To run with a different profile: `SPRING_PROFILES_ACTIVE=dev ./mvnw test`.

> CI workflow is included below; it runs tests on JDK 21 and uploads test reports.

## Async & Scheduling Notes
- `@EnableAsync` and a pooled **ThreadPoolTaskExecutor** (`weather-*` thread prefix).
- `@EnableScheduling`: a job iterates subscriptions in pages and sends emails concurrently.
- `NotificationService#send` is asynchronous — tests assert that work completes off the `main` thread.

## Database Migrations
Flyway scripts:
- `V1__create_subscription_table.sql`
- `V2__add_unique_constraint_to_subscription.sql`
- `V3__add_lower_email_city_index.sql` (unique on `lower(email), city` to prevent case-duplicates)

## What this project demonstrates (for resume)
- Clean REST design with validation and error mapping
- Persistence + schema evolution with Flyway
- Async processing, thread pools, and scheduled tasks
- External HTTP integration with timeouts and logging
- Containerization and env-based configuration
- Solid tests with Mockito/JUnit 5

---

### How to import the workflow
Copy `.github/workflows/ci.yml` from this package to your repo (see file provided alongside this README).
