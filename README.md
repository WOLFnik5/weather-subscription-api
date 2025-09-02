# Weather Subscription API

Simple Spring Boot service that lets users subscribe to regular weather updates for a chosen city.
Subscriptions are persisted in PostgreSQL and database schema is managed via Flyway migrations.

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

### Docker
```
docker compose up --build
```
This starts the application and a PostgreSQL database.

### Email configuration

SMTP settings are configurable via environment variables and override the defaults in
`src/main/resources/application.properties`:

- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH`
- `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE`

For local development the `dev` profile configures a local SMTP server on `localhost:1025`
with authentication and TLS disabled. Tools like MailHog can capture messages sent during
development.

## API
- `POST /api/subscriptions` – create a new subscription. Example body: `{ "email": "user@example.com", "city": "Kyiv" }`
- `GET /api/subscriptions` – list all subscriptions.
- `DELETE /api/subscriptions/{id}` – remove a subscription.
