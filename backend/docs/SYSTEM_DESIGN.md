# URL Shortener System Design

## 1. Problem Statement

Build a service that converts long HTTP(S) URLs into compact short links and redirects users from the short link to the original destination.

## 2. Requirements

### Functional

- Create a short URL.
- Create a short URL with an optional custom alias.
- Redirect users from `/{shortCode}` to the long URL.
- Track basic click count and last-accessed time.
- Expire links after a configurable TTL or requested expiration time.
- Return clear errors for invalid URLs, unavailable aliases, and unknown codes.

### Non-Functional

- Redirects should be low latency.
- Short codes must be globally unique.
- The service should be stateless so multiple instances can run behind a load balancer.
- Data should survive application restarts.
- The database schema should be migration-controlled.
- Runtime health and metrics should be available.

## 3. Capacity Assumptions

Example target:

- 1 million new links per day.
- 100 million redirects per day.
- Read-heavy traffic with redirects dominating writes.
- 8-character Base62 codes provide about 218 trillion combinations.

The current implementation is ready for modest production traffic with PostgreSQL. At very high redirect volume, add Redis caching and async analytics writes.

## 4. API Design

### Create Short URL

`POST /api/v1/urls`

```json
{
  "longUrl": "https://spring.io/projects/spring-boot",
  "customAlias": "spring",
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

Response:

```json
{
  "shortCode": "spring",
  "shortUrl": "http://localhost:8080/spring",
  "longUrl": "https://spring.io/projects/spring-boot",
  "createdAt": "2026-06-09T00:00:00Z",
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

### Redirect

`GET /{shortCode}`

Returns `302 Found` with a `Location` header.

### Stats

`GET /api/v1/urls/{shortCode}/stats`

```json
{
  "shortCode": "spring",
  "longUrl": "https://spring.io/projects/spring-boot",
  "clickCount": 10,
  "createdAt": "2026-06-09T00:00:00Z",
  "expiresAt": "2027-01-01T00:00:00Z",
  "lastAccessedAt": "2026-06-09T10:30:00Z",
  "active": true
}
```

## 5. Storage Design

The `short_urls` table stores one row per short link.

Critical indexes:

- Unique index on `short_code` for fast redirects and collision protection.
- Index on `expires_at` for future cleanup jobs.
- Index on `created_at` for operational queries.

## 6. Short Code Generation

The application uses secure random Base62 generation.

Why:

- Avoids predictable sequential IDs.
- Works across multiple app instances without coordination.
- Database unique constraint handles the rare collision case.

Collision strategy:

1. Generate a candidate code.
2. Try to insert.
3. If the unique constraint fails, retry up to `max-create-attempts`.
4. Return `503` if capacity or entropy problems prevent allocation.

## 7. Redirect Flow

1. Client requests `GET /abc123`.
2. Service looks up `abc123`.
3. Service rejects missing, inactive, or expired records.
4. Service increments click count and last-accessed timestamp.
5. Service returns `302 Found`.

For very high throughput, move step 4 to an event stream such as Kafka, RabbitMQ, or SQS.

## 8. Security Controls

- Only `http` and `https` URLs are accepted.
- URLs with user-info are rejected.
- Localhost and configured blocked hosts are rejected.
- Request DTOs use Bean Validation.
- Errors use RFC 7807 `ProblemDetail` responses.

Production additions to consider:

- Rate limiting by IP or authenticated tenant.
- Abuse reporting and malware scanning.
- Private-network IP checks after DNS resolution.
- Authentication for custom aliases and analytics.

## 9. Reliability

- Flyway owns schema migrations.
- JPA validates the schema at startup.
- Optimistic locking protects row updates.
- Actuator exposes `/actuator/health` and metrics.

## 10. Scaling Plan

Start:

- One or more Spring Boot instances.
- PostgreSQL primary database.
- Load balancer in front of application instances.

Next:

- Add Redis for redirect cache.
- Publish click events asynchronously.
- Add read replicas for analytics.
- Add CDN or edge workers for extremely hot links.

Large scale:

- Shard by short-code hash.
- Archive expired links.
- Separate redirect service from management API.

## 11. Failure Modes

| Failure | Current Behavior | Future Hardening |
| --- | --- | --- |
| Duplicate code | Retry generation | Monitor collision rate |
| DB unavailable | Request fails | Circuit breaker and graceful error page |
| Hot short code | DB sees repeated reads | Redis or edge cache |
| Click write contention | Optimistic version conflict risk | Async click events |
| Abuse URL submitted | Basic validation blocks obvious cases | Reputation scanning |

## 12. Local Production Simulation

Run PostgreSQL and the app:

```bash
docker compose up --build
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```
