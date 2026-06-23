# generic-boot

A generic Spring Boot 3.4.1 / Java 21 starter template with JWT auth, role-based access control, Flyway migrations,
OpenAPI docs, and a reusable generic CRUD/filter architecture.

---

## Tech stack

| Area        | Technology                                |
|-------------|-------------------------------------------|
| Runtime     | Java 21, Spring Boot 3.4.1, WAR packaging |
| API         | Spring Web MVC, SpringDoc OpenAPI 2.6.0   |
| Security    | Spring Security, JWT (jjwt 0.13.0)        |
| Persistence | Spring Data JPA, Flyway, PostgreSQL 17    |
| Mapping     | MapStruct 1.6.2, Lombok 1.18.34           |
| Logging     | Log4j2 with MDC correlation IDs           |

---

## Running locally (IDE — no Maven build required)

### Prerequisites

- Java 21
- PostgreSQL 17 (local or Docker — see below)

### Environment variables (IntelliJ Run/Debug Configuration)

```
SPRING_PROFILES_ACTIVE=dev
APP_NAME=generic-boot
APP_VERSION=0.1.0-SNAPSHOT
JWT_SECRET=your-local-dev-secret          # optional, defaults to keit_secret
```

Or as VM options:

```
-Dspring.profiles.active=dev -DAPP_NAME=generic-boot
```

When no profile is set, `spring.profiles.default=dev` activates `application-dev.yaml` automatically.

### Database — Docker (quickest)

```bash
docker pull postgres:17.5-alpine
docker run -d --name postgres_db \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=pass \
  -e POSTGRES_DB=generic \
  -p 2345:5432 \
  postgres:17.5-alpine

docker exec -it postgres_db psql -U user -d generic -c "CREATE SCHEMA IF NOT EXISTS generic;"
docker exec -it postgres_db psql -U user -d generic -c "ALTER ROLE \"user\" SET search_path TO generic, public;"
```

### Database — local PostgreSQL

```sql
-- connect as postgres
CREATE DATABASE generic;
\c generic
CREATE ROLE "user" WITH LOGIN PASSWORD 'pass' SUPERUSER CREATEDB CREATEROLE INHERIT REPLICATION BYPASSRLS;
CREATE SCHEMA IF NOT EXISTS generic AUTHORIZATION "user";
ALTER ROLE "user" SET search_path TO generic;
CREATE EXTENSION IF NOT EXISTS unaccent;
ALTER EXTENSION unaccent SET SCHEMA generic;
```

Flyway migrations run automatically on startup and create all tables + seed data.

---

## Profiles

| Profile | Purpose                                                                                     |
|---------|---------------------------------------------------------------------------------------------|
| `dev`   | Long JWT expiry (86400s), permissive CORS, verbose SQL logs off, context path `/{APP_NAME}` |
| `prod`  | Short JWT expiry (600s/900s), strict CORS, context path `/`                                 |

Activate via `SPRING_PROFILES_ACTIVE=prod` or `-Dspring.profiles.active=prod`.

---

## Security model

- **Stateless JWT** — no server-side session. Access tokens (10 min default) + refresh tokens (15 min default).
- **Session tracking** — issued JTIs are stored in `user_sessions`; logout revokes both tokens.
- **CSRF** — disabled for this API (Bearer tokens in `Authorization` headers are not CSRF-prone).
- **CORS** — configured per profile via `security.cors.allowed.origins`.
- **Header validation** — `AppHeaderValidatorFilter` checks `app-called-service` header.

### Default credentials (dev seed)

| Username | Password | Role |
|----------|----------|------|
| `keit`   | `keit`   | ADM  |

### Auth flow

```
POST /auth/register     → { verificationToken }
PUT  /auth/verify?t=... → enables account
POST /auth/login        → { token, refreshToken, expireAt, expireRefreshAt }
POST /auth/refresh      → new token pair (pass refresh token in X-Refresh-Token header)
POST /auth/logout       → revokes session
POST /auth/check        → validates session
```

---

## Main endpoints

All endpoints are prefixed with `/api/v1` (configurable via `server.servlet.api-path`).

| Endpoint                 | Auth            | Description                                          |
|--------------------------|-----------------|------------------------------------------------------|
| `GET /version`           | public          | App name, version, build, environment                |
| `GET /auth/**`           | public          | Authentication flows                                 |
| `GET /public/**`         | public          | OpenAPI docs                                         |
| `GET /users`             | authenticated   | Paginated user search (`?page=&size=&sort=&search=`) |
| `GET /users/{username}`  | authenticated   | Find user by username                                |
| `GET /users/id/{id}`     | authenticated   | Find user by UUID                                    |
| `GET /roles/code/{code}` | authenticated   | Find role by code                                    |
| `GET /watchdog/**`       | `WTC` authority | Watchdog-only endpoints                              |

---

## OpenAPI / Swagger UI

Available at:

```
http://localhost:9891/{APP_NAME}/public/swagger-ui
```

Click **Authorize** and paste a Bearer token obtained from `POST /auth/login`.

---

## Build (packaged WAR)

```bash
mvn clean package -Pdev
# Output: dist/generic.war
```

Build metadata (`build`, `buildTime`) is generated by the custom `build-metadata-maven-plugin` and injected via
`build.properties` at resource-filtering time.

---

## Observability

- **Health**: `GET /actuator/health` — liveness/readiness probes
- **Info**: `GET /actuator/info` — app name, version, build metadata
- **Logs**: `dist/logs/generic-{version}-b{build}.log` — rolling daily, 200 MB max
- **Correlation IDs**: every request receives an `X-Correlation-ID` header (generated if absent); included in all log
  lines and API response envelope

---

## Response envelope

All success responses follow:

```json
{
  "status": "OK",
  "timestamp": "2025-01-01T00:00:00Z",
  "message": null,
  "requestId": "550e8400-...",
  "meta": {
    "page": 0,
    "size": 20,
    "totalElements": 42,
    "totalPages": 3
  },
  "content": {
    ...
  }
}
```

Errors follow RFC 9457 `ProblemDetail`:

```json
{
  "type": "urn:error:not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Resource not found: username"
}
```

---

## Postman

Collections available in `src/main/resources/postman/`. Import `environment.variables.json` and `global.variables.json`
first.
