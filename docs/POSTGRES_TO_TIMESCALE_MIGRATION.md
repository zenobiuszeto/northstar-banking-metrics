# PostgreSQL to TimescaleDB Migration Guide

TimescaleDB is PostgreSQL with a time-series extension. Customer and account master data remain ordinary relational tables; high-volume transaction history becomes a hypertable partitioned by `occurred_at`.

## Recommended target architecture

```text
Core banking PostgreSQL
        │
        ├── one-time bulk load ──► TimescaleDB
        │                            ├── customers (relational)
        │                            ├── accounts (relational)
        │                            └── transactions (hypertable)
        │
        └── optional CDC ─────────► ongoing inserts and updates
                                     │
                                     └── Spring Boot metrics API
```

The source database remains authoritative until validation and cutover are approved. The supplied migration does not delete or modify source data.

## Prerequisites

- Source PostgreSQL and target TimescaleDB versions supported by your company.
- Network access from the migration host to both databases.
- `psql`, `pg_dump`, and `pg_restore` from a compatible PostgreSQL client release.
- A source account with consistent-read access and a target account allowed to create tables, extensions, and indexes.
- Enough encrypted temporary disk for the exported data.
- A tested backup, rollback owner, maintenance window, and change record.

## Schema mapping

| Source concept | Target | Treatment |
|---|---|---|
| Customer master | `customers` | Standard PostgreSQL table |
| Deposit accounts | `accounts` | Standard PostgreSQL table with customer foreign key |
| Transactions | `transactions` | TimescaleDB hypertable partitioned on `occurred_at` |
| Transaction identifier | Composite primary key `(id, occurred_at)` | Timescale unique constraints must include the partition column |

If company source names differ, create a staging view that exposes the columns expected by the migration script. This keeps source-specific transformations outside the target schema.

## One-time migration

1. Provision an empty TimescaleDB target and enable encrypted connections.
2. Stop the application seed with `SEED_DEMO_DATA=false`; production data must never be mixed with demo data.
3. Run preflight counts, date-range checks, duplicate checks, and source-to-target type mapping.
4. Run the supplied `scripts/migrate-postgres-to-timescale.sh` from the repository root. It applies the same versioned baseline schema used by Flyway before loading data.
5. Validate row counts, transaction totals, min/max timestamps, null rates, and a sample of customer-account-transaction joins.
6. Run dashboard aggregate reconciliation against approved source reports.
7. Point the Spring Boot application to the target only after business and technical sign-off.

Required environment variables:

```bash
export SOURCE_DATABASE_URL='postgresql://readonly_user:password@source-host:5432/source_db?sslmode=require'
export TARGET_DATABASE_URL='postgresql://migration_user:password@target-host:5432/northstar?sslmode=require'
SEED_DEMO_DATA=false ./scripts/migrate-postgres-to-timescale.sh
```

Avoid placing passwords in shell history in a real deployment. Prefer a secrets manager, `.pgpass` with restricted permissions, workload identity, or your company's credential broker.

## Low-downtime production cutover

For a large or continuously changing source:

1. Record a source log sequence number or consistent snapshot boundary.
2. Bulk-load historical customers, accounts, and transactions.
3. Start change data capture from the boundary using an approved tool such as Debezium, cloud database migration services, or your enterprise integration platform.
4. Validate replication lag and source/target reconciliation.
5. Place writes in a short controlled window, drain the remaining changes, and validate again.
6. Switch application reads, keep rollback routing available, and monitor errors, freshness, and aggregate drift.

## Validation queries

Compare these on source and target using the agreed transformation rules:

```sql
select count(*) from customers;
select count(*) from accounts;
select count(*), min(occurred_at), max(occurred_at), sum(amount) from transactions;
select transaction_type, count(*), sum(amount)
from transactions
group by transaction_type
order by transaction_type;
```

Confirm the TimescaleDB table is a hypertable:

```sql
select hypertable_schema, hypertable_name, num_dimensions
from timescaledb_information.hypertables
where hypertable_name = 'transactions';
```

## Rollback

- Keep the source database unchanged and authoritative during migration.
- Do not decommission the source or CDC stream until the agreed stabilization period ends.
- If cutover checks fail, restore application routing to the source, preserve target diagnostics, and replay from the last validated boundary after correction.
- A rollback should not reverse-copy unvalidated target writes into the source.

## Production hardening

- Replace demo credentials and disable automatic demo seeding.
- Use TLS, least-privilege database roles, secret rotation, audit logging, and network allowlists.
- Review and promote the included Flyway migrations through the normal database change process; do not run ad hoc schema creation.
- Define hypertable chunk intervals, compression/columnstore policy, retention, backups, and disaster recovery from measured data volume and query patterns.
- Tokenize or mask customer identifiers in analytical environments unless clear-text access is explicitly approved.
