---
title: Testing
domain: guides
tags: [tests, testcontainers, quality]
status: current
last_updated: 2026-08-13
---

# Testing

Use plain unit tests for policies, provider failures, mapping, and service decisions. Use PostgreSQL-backed JPA slices for mappings and constraints, and `@SpringBootTest` for MVC, security, OpenAPI, MCP, Flyway, and application wiring.

Every test uses Arrange/Act/Assert markers and behavior names. Firebase and AI tests mock their SDK/client boundaries; automated tests never use live credentials or paid providers.

Docker is required for the full suite:

```bash
./mvnw clean compile
./mvnw test
./mvnw package -DskipTests
```

Do not run Maven concurrently in one checkout because all runs share `target/`. If a logically impossible test class appears after large deletions or moves, clean test bytecode before diagnosing product code.
