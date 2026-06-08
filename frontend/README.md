# Shortlink Console Frontend

React frontend for the Spring Boot URL shortener.

## Run

Start the Spring Boot backend first:

```bash
cd ../backend
mvn spring-boot:run
```

Then start the frontend:

```bash
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

The Vite dev server proxies `/api` and `/actuator` requests to `http://localhost:8080`.

## Build

```bash
npm run build
```

## Features

- Create generated short URLs.
- Create custom aliases.
- Set optional expiration time.
- Copy and open generated links.
- Check API health.
- Lookup stats by short code.
- Persist recent generated links in local browser storage.
