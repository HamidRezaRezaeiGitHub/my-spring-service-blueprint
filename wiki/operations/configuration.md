---
title: Configuration
domain: operations
tags: [configuration, profiles, secrets]
status: current
last_updated: 2026-08-17
---

# Configuration

The default profile always enables the stateless security chain, Flyway, and Hibernate validation while selecting `noop` authentication/storage and disabling Firebase, MCP, and every AI model. It is safe to start without cloud credentials or an OpenAI key. Security has no environment property that can disable it accidentally.

`dev`, `uat`, and `production` configure database and operational behavior without selecting a cloud provider. `mcp` and `openai` are additive opt-in profiles. Runtime secrets come from environment variables or mounted resources; they never belong in YAML, Git, or the container image.

UAT and production require `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. Production disables generated OpenAPI endpoints. See [security/providers](../security/providers.md) before enabling Firebase or storage.

AI prompt bounds, Firebase timeouts, storage provider selection, maximum file size, and signed-URL duration are validated during configuration binding. Invalid values fail startup instead of surfacing during a request.
