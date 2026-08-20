# Northstar Banking Metrics

Northstar is an enterprise-quality, production-shaped reference dashboard that banking organizations can evaluate and adapt. It presents a synthetic 100,000-customer deposit portfolio across business and consumer checking and savings, with executive, growth, retention, risk, and technology KPIs.

It is intended for banking product/analytics leaders, architects, engineers, security teams, and data-governance teams evaluating an adaptable Java 21, React, PostgreSQL, and TimescaleDB baseline. All values are synthetic. Northstar is not a system of record, regulatory certification, security audit, or production approval and must not be used for financial reporting without governed sources and certified formulas.

> **Security and licensing:** Do not load real customer data or deploy this repository as-is. Required enterprise controls and known gaps are in [Production readiness](docs/PRODUCTION_READINESS.md) and the [Threat model](docs/THREAT_MODEL.md). No open-source license has yet been authorized; [owner action is required](LICENSE-DECISION.md) before describing or distributing the project as open source.

## What you get

- Premium minimal React dashboard with portfolio and product views, responsive layout, and loading/error/empty states.
- Spring Boot API with clean domain/application/adapter/API seams, validated requests, RFC 9457 problem responses, typed configuration, graceful shutdown, and Actuator probes.
- A banking domain spanning customers, products, applications, accounts, transactions, fraud, pricing, snapshots, and governed metrics; TimescaleDB schema is managed by Flyway.
- Opt-in, deterministic, batched demo generation for 100,000 customers, protected by a PostgreSQL advisory lock for multi-instance startup.
- Container health checks, configurable runtime values, non-root API image, and an Nginx same-origin API proxy.

## Architecture

```text
Browser
  └── Nginx / React
        └── /api proxy
              └── API controller + DTO mapper
                    └── metrics query service
                          └── AnalyticsProjectionRepository port
                                └── deterministic DEMO projection adapter

Spring Boot startup
  ├── Flyway migrations ──► PostgreSQL / TimescaleDB
  └── demo-profile seed ──► bounded operational facts and analytical snapshots
```

Backend packages under `server/src/main/java/com/northstar/metrics`:

| Package | Responsibility |
|---|---|
| `domain` | Operational banking entities/value objects plus governed analytics models and invariants |
| `application` | Focused account activity, metric lineage, dashboard queries, and repository ports |
| `api` | HTTP controller, response mapping, and problem handling |
| `infrastructure.metrics` | Deterministic analytics projection and focused JDBC operational/lineage adapters |
| `infrastructure.seed` | Isolated synthetic data generation and persistence |
| `config` | Typed settings, clock, and CORS policy |

Frontend modules under `web/src` separate app composition, the metrics feature (API, hook, model, dashboard), reusable components, and shared formatting utilities.

## Quick start

Prerequisites: Docker with Compose. Allocate enough memory and disk for roughly 100,000 customers, 111,000 accounts, and more than 500,000 transactions.

```bash
cp .env.example .env
docker compose up --build
```

Open [http://localhost:3000](http://localhost:3000). The API readiness probe is available inside the Compose network at `/actuator/health/readiness`. Compose activates the explicit `demo` profile. First startup can take several minutes while bounded batches generate the richer synthetic model; later starts reuse the named database volume. The application default and `production` profile never seed.

Stop services with `docker compose down`. Add `--volumes` only when you intentionally want to delete the local database and regenerate it.

### Local development

Start TimescaleDB, then run the API and UI separately:

```bash
docker compose up -d postgres
cd server && SPRING_PROFILES_ACTIVE=demo SEED_DEMO_DATA=true ./gradlew bootRun
cd web && npm ci && npm run dev
```

Vite proxies `/api` to `http://localhost:8080`; deployed web traffic uses the Nginx proxy. Set `VITE_API_BASE_URL` only when the browser must call a different API origin.

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/northstar` | JDBC connection URL |
| `DATABASE_USER` / `DATABASE_PASSWORD` | `northstar` | Database credentials; override outside local development |
| `DATABASE_POOL_MAX_SIZE` | `10` | Maximum HikariCP connections per API replica |
| `SEED_DEMO_DATA` | `false` in the app, `true` in Compose | Enables generation only when the `demo` profile is also active |
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
- [Architecture](docs/ARCHITECTURE.md) and [ADRs](docs/adr/0001-modular-monolith.md) — boundaries and major decisions.
- [Production readiness](docs/PRODUCTION_READINESS.md) and [threat model](docs/THREAT_MODEL.md) — deployment controls and known gaps.
- [API compatibility](docs/API_COMPATIBILITY.md) — 1.x evolution policy.
- [Contributing](CONTRIBUTING.md), [security reporting](SECURITY.md), and [code of conduct](CODE_OF_CONDUCT.md).

## Production boundaries

The API currently serves deterministic dashboard metrics through a repository adapter so the UI remains stable while a governed aggregate store is integrated. Seeded operational facts and demo snapshots demonstrate domain/storage scale but do not calculate every displayed KPI. Before deployment, replace the demo adapter, activate the validated `production` profile with non-default credentials and restricted CORS, enforce OIDC/RBAC, isolate management endpoints, add immutable audit export and SLO alerts, and complete reconciliation, threat-model, privacy, legal, and data-governance review.
