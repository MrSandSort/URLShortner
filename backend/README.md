# URL Shortener - Spring Boot

Production-grade URL shortener with a clean API, persistent storage, migrations, validation, redirect handling, health checks, and a deployment-ready PostgreSQL profile.

## System Design

### Functional Requirements

- Create a short URL for an HTTP or HTTPS long URL.
- Optionally reserve a custom alias.
- Redirect `/{shortCode}` to the original URL.
- Show basic analytics for a short URL.
- Support expiry for links.

### Non-Functional Requirements

- Low-latency redirects.
- Unique, hard-to-guess short codes.
- Safe input validation.
- Database-backed durability.
- Horizontal scalability with stateless application instances.
- Observable runtime through actuator health and metrics endpoints.

### High-Level Architecture

```text
Client
  |
  v
Spring Boot API
  |-- POST /api/v1/urls
  |-- GET  /{shortCode}
  |-- GET  /api/v1/urls/{shortCode}/stats
  |
  v
PostgreSQL / H2
```

### Data Model

`short_urls`

| Column | Purpose |
| --- | --- |
| `short_code` | Public identifier, unique indexed lookup key |
| `long_url` | Original destination URL |
| `created_at` | Creation timestamp |
| `expires_at` | Optional expiration timestamp |
| `active` | Enables future soft-disable behavior |
| `click_count` | Basic analytics counter |
| `last_accessed_at` | Last redirect timestamp |
| `version` | Optimistic locking support |

### Code Generation

The application uses `SecureRandom` with a Base62 alphabet. Generated codes are protected by a database unique constraint and retried on rare collisions.

With 62 characters and length 8, the address space is:

```text
62^8 = 218,340,105,584,896 possible codes
```

### Scaling Path

- Put the Spring Boot service behind a load balancer.
- Use PostgreSQL as the source of truth.
- Add Redis or Caffeine for hot redirect cache entries.
- Move click tracking to an async queue when write volume grows.
- Add rate limiting at the API gateway or with Bucket4j.
- Partition or archive old URLs when the table becomes very large.

## Run Locally

This project is Maven-based.

```bash
mvn spring-boot:run
```

Create a short URL:

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://spring.io/projects/spring-boot"}'
```

Create one with a custom alias:

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://spring.io","customAlias":"spring"}'
```

Redirect:

```bash
curl -i http://localhost:8080/spring
```

Stats:

```bash
curl http://localhost:8080/api/v1/urls/spring/stats
```

## Production Profile

```bash
SPRING_PROFILES_ACTIVE=prod \
DATABASE_URL=jdbc:postgresql://localhost:5432/urlshortener \
DATABASE_USERNAME=urlshortener \
DATABASE_PASSWORD=change-me \
PUBLIC_BASE_URL=https://sho.rt \
mvn spring-boot:run
```

## API Summary

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/urls` | Create a short URL |
| `GET` | `/{shortCode}` | Redirect to long URL |
| `GET` | `/api/v1/urls/{shortCode}/stats` | Fetch analytics |
| `GET` | `/actuator/health` | Health check |

## Notes

- Local development uses H2 in PostgreSQL compatibility mode.
- Production uses Flyway migrations and validates the schema at startup.
- The application rejects non-HTTP(S), localhost, user-info, and configured blocked-host URLs.
