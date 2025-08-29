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

## API
- `POST /api/subscriptions` – create a new subscription. Example body: `{ "email": "user@example.com", "city": "Kyiv" }`
- `GET /api/subscriptions` – list all subscriptions.
- `DELETE /api/subscriptions/{id}` – remove a subscription.
