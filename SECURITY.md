# Security Policy

Northstar is an executable reference implementation and has not been certified, audited, or approved for regulated workloads. Do not deploy it with customer data as-is.

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability. Repository owners should configure a private security-reporting address and GitHub private vulnerability reporting before public release. Until then, contact the repository owner through a private organizational channel. Include affected versions, reproduction details, impact, and suggested mitigation; do not include real banking data.

## Supported versions

Only the current default branch is maintained during the evaluation phase. The owner must publish a supported-version and disclosure timeline before a public release.

## Deployment expectations

Operators are responsible for OIDC/RBAC, TLS, network policy, secret and key management, dependency/container scanning, audit-event export, database encryption and backups, retention/deletion controls, monitoring, incident response, and all regulatory obligations. See `docs/THREAT_MODEL.md` and `docs/PRODUCTION_READINESS.md`.
