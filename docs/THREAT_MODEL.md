# Threat model

## Scope and assets

Protected assets in a real adaptation include customer identity mappings, account and transaction facts, application decisions, fraud investigations, balances, metric formulas, credentials, encryption keys, audit evidence, and availability. The included dataset is synthetic and contains no intentionally real PII.

## Trust boundaries and principal threats

| Boundary | Threats | Required production controls |
|---|---|---|
| Browser to edge | Session theft, injection, clickjacking, excessive data access | TLS, OIDC, short-lived secure cookies/tokens, CSP, CSRF policy, authorization, rate limiting, WAF where justified |
| Edge to API | Spoofed identity/headers, request abuse | Trusted proxy configuration, token validation, scopes/roles, input limits, correlation IDs, throttling |
| API to database | Credential theft, SQL abuse, lateral movement | Workload identity or rotated secrets, TLS, least-privilege roles, network policy, prepared statements |
| Data pipelines to projections | Poisoned/late/duplicate data, formula tampering | Immutable source IDs, watermarks, reconciliation, approvals, lineage, versioned formulas, restatement controls |
| Operations plane | Management endpoint leakage, image/dependency compromise | Separate management network/port, signed images/SBOM, scanning, patch SLAs, restricted logs and metrics |

## Banking-specific risks

Never log customer identifiers, account numbers, tokens, application decisions, or transaction payloads without approved masking. Fraud and model outputs require tighter access and audit trails. Authorization must be server-side and preferably attribute-aware for business unit, legal entity, region, and purpose. Encryption keys belong in managed KMS/HSM systems with separation of duties and rotation. Retention, legal holds, deletion, consent, residency, and cross-border transfers must be designed with legal/privacy teams.

## Known reference gaps

OIDC/RBAC, immutable audit export, rate limiting, field encryption/tokenization, malware/DLP controls, key management, consent, production network policy, and incident integrations are extension points. Their absence is intentional and prevents a production-readiness claim.
