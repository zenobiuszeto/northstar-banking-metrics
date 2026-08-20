# ADR 0003: TimescaleDB for append-heavy facts

Status: accepted.

Keep customer, product, application, account, fraud-case, metric-definition, snapshot, and daily aggregate tables relational. Use hypertables for high-volume transactions and rate history, whose primary keys include the time partition. Additional hypertables require measured volume and query evidence; TimescaleDB does not replace relational modeling or metric governance.
