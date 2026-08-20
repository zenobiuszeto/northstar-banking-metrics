# Production-readiness guide

This repository is a production-shaped baseline, not production approval or regulatory certification. An adopting bank owns design review, control evidence, risk acceptance, and jurisdiction-specific obligations.

## Required integrations

- Put TLS and an authenticated edge in front of the web/API. Integrate OIDC and map claims to least-privilege RBAC/ABAC in the application layer.
- Replace demo projections with governed sources. Reconcile to core banking/general ledger controls, assign owners, version formulas, capture source watermarks, and define late-data restatement.
- Send security/business audit events to an immutable, access-controlled sink. Correlation logs are operational diagnostics, not an audit ledger.
- Use managed secrets and KMS/HSM-backed encryption. Enable database/storage encryption, TLS verification, rotation, revocation, and break-glass procedures.
- Classify and minimize PII; implement tokenization/masking, residency, retention, deletion, legal hold, and access-review policies.

## Reliability and scale

Run API replicas statelessly behind a load balancer. Size Hikari pools so `replicas × maximum-pool-size` stays below database connection capacity. Set CPU/memory requests and limits from load tests; the image uses JVM container ergonomics but that is not capacity planning. Use explicit client, proxy, JDBC, statement, and shutdown timeouts.

Run Flyway as a single controlled release step with a least-privilege migration identity; application identities should not own DDL in mature environments. Test forward and rollback/roll-forward procedures on production-like data. Use multi-zone database HA, point-in-time recovery, encrypted backups, restore drills, defined RPO/RTO, replica-lag alerts, and capacity alarms. Timescale chunk, compression, and retention policies must follow measured ingest/query patterns.

Separate liveness (process can run) from readiness (dependencies can serve). Restrict Actuator to an operations network, scrape Prometheus with authentication where supported, redact labels, and alert on availability, latency, saturation, errors, connection pools, migration failures, data freshness, projection drift, and seed activation. Export structured logs centrally with redaction and retention controls.

## Deployment reference

Compose is only a local/demo path. For Kubernetes, use separate Deployments/Services for web and API, a managed database, Secrets/External Secrets, NetworkPolicies, PodDisruptionBudgets, topology spread, autoscaling based on tested signals, separate readiness/liveness probes, and a Flyway Job. For Cloud Run or another serverless container platform, use a managed database connector, bounded instance concurrency, minimum instances where cold starts matter, secret injection, and connection pooling appropriate to instance scaling. Pin images by digest in released environments.

## Release gate

Require threat-model review, dependency/container/SBOM checks, API compatibility tests, migration rehearsal, performance and failure testing, backup restoration, reconciliation, privacy/security review, runbooks, monitoring, rollback criteria, and named operational ownership before any production decision.
