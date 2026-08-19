# Northstar Banking Metrics

Full-stack implementation: React browser UI, Spring Boot 3 with JDK 21, and TimescaleDB (PostgreSQL).

## Documentation

- [Banking metrics catalog](docs/METRICS_CATALOG.md)
- [PostgreSQL to TimescaleDB migration guide](docs/POSTGRES_TO_TIMESCALE_MIGRATION.md)

Run TimescaleDB with `docker compose up -d`, then start the API with `./gradlew bootRun` from `server`. Start the UI with `npm install && npm run dev` from `web`. Transaction history is stored as a TimescaleDB hypertable, partitioned by occurrence time.
