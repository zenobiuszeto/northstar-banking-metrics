# ADR 0002: Separate analytics projections from operational data

Status: accepted.

Dashboard queries use `AnalyticsProjectionRepository`; they do not assemble HTTP responses from account/transaction entities. Operational repositories support focused account activity and metric-lineage use cases. Governed snapshots and daily product metrics carry formula versions and source watermarks. The current deterministic projection preserves the demo while making replacement explicit.
