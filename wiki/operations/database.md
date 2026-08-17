---
title: Database
domain: operations
tags: [postgresql, flyway, jpa]
status: current
last_updated: 2026-08-17
---

# Database

PostgreSQL 18 is used locally, in tests, and in deployment. [compose.yaml](../../compose.yaml) is the canonical local service definition. Testcontainers pins `postgres:18-alpine` and supplies connection details through `@ServiceConnection`.

Flyway migrations under `src/main/resources/db/migration/` are authoritative. Hibernate uses `ddl-auto=validate`; schema drift fails startup or tests rather than silently altering the database. Entities use standard JPA UUID generation and therefore have a null identifier until persistence. `AuditableEntity` assigns microsecond creation/update timestamps through JPA callbacks and deliberately does not define mutable audit-based equality.
