# ADR 0001: Modular monolith

Status: accepted for the reference implementation.

Use one Spring Boot deployment with domain, application, adapter, and API package boundaries. The current scale does not justify distributed transactions, independent service ownership, or microservice operational cost. Extract a service only when independent scaling, data ownership, release cadence, or team ownership is demonstrated.
