# Weather Subscription API

Simple Spring Boot service that lets users subscribe to regular weather updates for a chosen city.
Subscriptions are persisted in PostgreSQL and database schema is managed via Flyway migrations.

## Quick Start

```bash
docker compose up --build
```

This launches the application and a PostgreSQL database.

## Development

### Run tests
```bash
mvn test
```

### Run locally
```bash
./mvnw spring-boot:run
```
Requires PostgreSQL running at `localhost:5432` with database `weather` and user/password `postgres`/`postgres`.

To use an in-memory H2 database, run the application with the `dev` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Swagger UI

When the application is running, interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

## Environment Variables

### Database

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Mail

- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH`
- `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE`
- `APP_MAIL_FROM`

Outgoing emails are logged; during development a local tool such as MailHog can
be used to inspect messages.

## API

- `POST /api/subscriptions` – create a new subscription.

  ```bash
  curl -X POST http://localhost:8080/api/subscriptions \
    -H 'Content-Type: application/json' \
    -d '{"email":"user@example.com","city":"Kyiv"}'
  ```

- `GET /api/subscriptions` – list all subscriptions.

  ```bash
  curl "http://localhost:8080/api/subscriptions?page=0&size=20"
  ```

- `DELETE /api/subscriptions/{id}` – remove a subscription.

  ```bash
  curl -X DELETE http://localhost:8080/api/subscriptions/1
  ```

## What I Demonstrated in this Project

- Spring Boot REST API
- PostgreSQL with Flyway migrations
- Scheduled notifications
- Asynchronous email sending
- Docker Compose for local development
