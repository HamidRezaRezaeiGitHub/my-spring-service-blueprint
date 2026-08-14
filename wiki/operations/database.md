---
title: Database
domain: operations
tags: [postgresql, flyway, jpa]
status: current
last_updated: 2026-08-13
---

# Database

PostgreSQL 18 is used locally, in tests, and in deployment. [compose.yaml](../../compose.yaml) is the canonical local service definition. Testcontainers pins `postgres:18-alpine` and supplies connection details through `@ServiceConnection`.

Flyway migrations under `src/main/resources/db/migration/` are authoritative. Hibernate uses `ddl-auto=validate`; schema drift fails startup or tests rather than silently altering the database. Entities use application-generated UUIDs and microsecond audit timestamps.
