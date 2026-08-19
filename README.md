# Northstar Banking Metrics

Northstar is a production-shaped reference dashboard for banking leaders. It presents a synthetic 100,000-customer deposit portfolio across business and consumer checking and savings, with executive, growth, retention, risk, and technology KPIs.

The project is useful to product and analytics teams evaluating a banking dashboard, and to engineers looking for a maintainable Java 21, React, PostgreSQL, and TimescaleDB starting point. All values are synthetic. It is not a system of record and must not be used for financial reporting without governed sources and certified formulas.

## What you get

- Premium minimal React dashboard with portfolio and product views, responsive layout, and loading/error/empty states.
- Spring Boot API with clean domain/application/adapter/API seams, validated requests, RFC 9457 problem responses, typed configuration, graceful shutdown, and Actuator probes.
- TimescaleDB schema managed by Flyway, with relational customer/account masters and a transaction hypertable.
- Opt-in, deterministic, batched demo generation for 100,000 customers, protected by a PostgreSQL advisory lock for multi-instance startup.
- Container health checks, configurable runtime values, non-root API image, and an Nginx same-origin API proxy.

## Architecture

```text
Browser
  └── Nginx / React
        └── /api proxy
              └── API controller + DTO mapper
                    └── metrics query service
                          └── MetricsRepository port
                                └── demo metrics adapter

Spring Boot startup
  ├── Flyway migrations ──► PostgreSQL / TimescaleDB
  └── opt-in seed runner ──► customers, accounts, transactions
```

Backend packages under `server/src/main/java/com/northstar/metrics`:

| Package | Responsibility |
|---|---|
| `domain` | Product vocabulary and portfolio metric model |
| `application` | Use cases and repository ports |
| `api` | HTTP controller, response mapping, and problem handling |
| `infrastructure.metrics` | Current deterministic metrics adapter |
| `infrastructure.seed` | Isolated synthetic data generation and persistence |
| `config` | Typed settings, clock, and CORS policy |

Frontend modules under `web/src` separate app composition, the metrics feature (API, hook, model, dashboard), reusable components, and shared formatting utilities.

## Quick start

Prerequisites: Docker with Compose. Allocate enough memory and disk for roughly 100,000 customers, 111,000 accounts, and more than 500,000 transactions.

```bash
cp .env.example .env
docker compose up --build
```

Open [http://localhost:3000](http://localhost:3000). The API readiness probe is available inside the Compose network at `/actuator/health/readiness`. First startup can take several minutes while the demo dataset is generated; later starts reuse the named database volume.

Stop services with `docker compose down`. Add `--volumes` only when you intentionally want to delete the local database and regenerate it.

### Local development

Start TimescaleDB, then run the API and UI separately:

```bash
docker compose up -d postgres
cd server && SEED_DEMO_DATA=true ./gradlew bootRun
cd web && npm ci && npm run dev
```

Vite proxies `/api` to `http://localhost:8080`; deployed web traffic uses the Nginx proxy. Set `VITE_API_BASE_URL` only when the browser must call a different API origin.

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/northstar` | JDBC connection URL |
| `DATABASE_USER` / `DATABASE_PASSWORD` | `northstar` | Database credentials; override outside local development |
| `DATABASE_POOL_MAX_SIZE` | `10` | Maximum HikariCP connections per API replica |
| `SEED_DEMO_DATA` | `false` in the app, `true` in Compose | Enables synthetic generation only for demo environments |
| `SEED_CUSTOMER_COUNT` | `100000` | Synthetic customer count, validated from 1 to 1,000,000 |
| `SEED_BATCH_SIZE` | `1000` | JDBC seed batch size, validated from 100 to 10,000 |
| `CORS_ALLOWED_ORIGINS` | local Vite origins | Comma-separated browser origins for direct API development |
| `SERVER_PORT` / `WEB_PORT` | `8080` / `3000` | API container and host web ports |

Use a secret manager and TLS in shared or production environments. Do not commit a populated `.env` file.

## Validation

```bash
cd server && ./gradlew clean test bootJar
cd web && npm ci && npm test && npm run build
docker compose config --quiet
```

## Major metrics and documentation

The experience covers total deposits, net new money, margin, fraud loss, applications and activation, relationship engagement, retention/churn, at-risk balances, acquisition economics, pricing, availability, latency, and data freshness.

- [Banking metrics catalog](docs/METRICS_CATALOG.md) — definitions, audiences, cadence, and governance.
- [Banking analytics data model](docs/DATA_MODEL.md) — current schema, recommended production facts, lineage, and controls.
- [PostgreSQL to TimescaleDB migration guide](docs/POSTGRES_TO_TIMESCALE_MIGRATION.md) — bulk migration, CDC, validation, cutover, and rollback.

## Production boundaries

The API currently serves deterministic dashboard metrics through a repository adapter so the UI remains stable while a governed aggregate store is integrated. Seeded transaction data demonstrates scale and TimescaleDB storage but does not yet calculate every displayed KPI. Before deployment, replace the demo adapter, disable seeding, enforce authenticated/authorized access, restrict management endpoints, add telemetry export and SLO alerts, and complete financial reconciliation and data-governance review.
