---
title: System Overview
domain: architecture
tags: [architecture, rest, mcp, persistence]
status: current
last_updated: 2026-08-13
---

# System Overview

The service is a modular monolith. A feature owns its use cases, DTOs, persistence, and resource authorization. REST controllers and MCP tools are independent inbound adapters and call the same feature service; neither adapter calls the other.

PostgreSQL is the only database. Flyway owns schema changes and Hibernate validates mappings. External authentication and object storage sit behind local provider interfaces. The default providers authenticate nobody and perform no storage operations.

`hello` demonstrates adapter parity. `ai` demonstrates an optional outbound model call that remains present as a clear 503 contract when no chat model is configured.
