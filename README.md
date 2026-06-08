# URL Shortener Workspace

This workspace is split into two production-style projects:

```text
backend/   Spring Boot API, persistence, redirects, stats, migrations
frontend/  React + Vite UI for creating and managing short URLs
```

## Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

## Run Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

The frontend dev server proxies API calls to the backend on port `8080`.

## Build Checks

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm run build
```

## Docker Backend With PostgreSQL

From the workspace root:

```bash
docker compose up --build
```
