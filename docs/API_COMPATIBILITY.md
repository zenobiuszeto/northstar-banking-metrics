# API versioning and compatibility

The current evaluation endpoint is `GET /api/metrics?product=...`. During the 1.x reference phase, existing response fields retain meaning and type; additive optional fields are allowed. Removing/renaming fields, changing units, changing product vocabulary, or altering error semantics requires a versioned endpoint (for example `/api/v2`) and a documented migration window.

Clients must ignore unknown fields and handle RFC 9457 problem responses, timeouts, and unavailable data. Metric formula changes are data-governance changes even when the JSON schema is stable: publish formula version/effective date and preserve historical interpretation. No long-term support window is promised until the owner publishes a release policy.
