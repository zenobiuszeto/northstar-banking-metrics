# Contributing

Thank you for helping improve Northstar Banking Metrics. This repository is an evaluation reference, not a certified banking product.

## Development workflow

1. Open an issue describing the behavior, architectural impact, and any banking-data assumptions.
2. Keep changes inside the existing module boundaries documented in `docs/ARCHITECTURE.md`; propose boundary changes with an ADR.
3. Add or update tests for invariants, mappings, API compatibility, and UI behavior.
4. Run `./gradlew clean test bootJar` in `server`, then `npm ci && npm test && npm run build` in `web`.
5. Do not include real customer data, credentials, access tokens, proprietary formulas, or production logs.

Changes to metric formulas require an owner, lineage, formula version, effective date, reconciliation evidence, and an explicit certification state. Security-sensitive changes should follow `SECURITY.md` rather than a public issue.

## Pull requests

Keep commits focused and describe operational risks, migrations, rollback, compatibility, and deferred work. By contributing, you confirm you have the right to submit the work. A project license has not yet been selected; see `LICENSE-DECISION.md` before accepting external contributions.
