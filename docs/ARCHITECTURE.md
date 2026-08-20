# Architecture

Northstar is a modular monolith reference implementation. It demonstrates enforceable seams without inventing distributed services that the current workload does not require.

## Runtime containers

- `web`: static React application served by Nginx; proxies same-origin `/api` requests.
- `api`: stateless Spring Boot process; owns HTTP policy and application use cases.
- `postgres`: local TimescaleDB for demo only. A managed PostgreSQL/Timescale service is expected for deployment.

API replicas can scale horizontally after Flyway is assigned to one deployment job or leader. Synthetic seeding is demo-profile-only and uses a PostgreSQL advisory lock. The deterministic dashboard projection is process-local and identical across replicas; replacing it with governed aggregates is the main data integration seam.

## Code boundaries

```text
api ──► application ──► domain
          │                ▲
          └── ports        │
               ▲           │
infrastructure adapters ───┘
```

- `domain`: customer, product, account, application, transaction, fraud, rate, snapshot, governed metric, money, and portfolio-scope concepts with invariants.
- `application`: focused queries/use cases plus projection, account, transaction, and metric-definition ports.
- `infrastructure.metrics`: deterministic analytics projection and JDBC operational/lineage adapters.
- `infrastructure.seed`: demo-only synthetic generation and persistence orchestration.
- `api`: version-neutral `/api` compatibility endpoint, DTO mapping, validation, correlation, and safe problem responses.

Operational banking records are not dashboard DTOs. `AnalyticsProjectionRepository` is the CQRS-style read seam. `DailyAccountSnapshot` and `GovernedMetric` make lineage from operational facts to certified read models explicit; the current dashboard adapter remains `DEMO`, not certified.

See ADRs for the [modular monolith](adr/0001-modular-monolith.md), [projection boundary](adr/0002-analytics-projection.md), and [Timescale storage](adr/0003-timescale-storage.md).
