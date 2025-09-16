# Weather Subscription API

[![CI](https://github.com/<your-username>/weather-subscription-api/.github/workflows/ci.yml)](https://github.com/<your-username>/weather-subscription-api/.github/workflows/ci.yml)
![Coverage](.github/badges/jacoco.svg)

A Spring Boot application for managing weather forecast subscriptions with email notifications.

---

## Features
- REST API for creating and listing subscriptions
- DTOs with validation annotations
- Database migrations (Flyway)
- Async email sending with Mailhog (via `@Async`)
- Integration tests with **Testcontainers** (Postgres, Mailhog)
- Mappers implemented using **MapStruct**
- Global exception handling
- Quality checks: **Checkstyle**, **SpotBugs**, **JaCoCo coverage**

---

## Requirements
- Java 21+
- Maven 3.9+
- Docker (needed for Testcontainers)

---

## Run locally

### With Docker Compose
```bash
docker compose up -d
```
Then open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### With Maven
```bash
./mvnw spring-boot:run
```

---

## Running tests
Integration tests require Docker to be running (PostgreSQL and Mailhog containers are started automatically by **Testcontainers**).

```bash
./mvnw verify
```

This will run:
- **Unit tests**
- **Integration tests**
- **Checkstyle**
- **SpotBugs**
- **JaCoCo coverage**

Reports:
- JaCoCo: `target/site/jacoco/index.html`
- Checkstyle: `target/site/checkstyle.html`
- SpotBugs: `target/spotbugs.html`

---

## Project structure
```
src/main/java/com/example/weather
 ├── config/           # Async config
 ├── controller/       # REST controllers
 ├── dto/              # Data Transfer Objects
 │    └── request/     # Request DTOs
 ├── entity/           # JPA entities
 ├── exception/        # Custom exceptions & global handler
 ├── mapper/           # MapStruct mappers
 ├── repository/       # Spring Data repositories
 └── service/          # Business logic
```

---

## Roadmap / TODO
- [ ] Add SonarCloud analysis
- [ ] CI matrix for Java 17 + 21
- [ ] Deployment workflow (Docker image publishing)
