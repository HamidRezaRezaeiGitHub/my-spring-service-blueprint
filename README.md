# Spring Service Blueprint

A reusable Java 25 / Spring Boot 4 backend template with feature-first packages, PostgreSQL 18, Flyway, stateless Bearer security, optional Firebase adapters, REST and MCP parity, and optional OpenAI text generation.

The default configuration is credential-free: Firebase, object storage, MCP, and chat models are disabled or fail closed. Start with [the wiki](wiki/index.md) for architecture and operating guidance.
When creating a new service, follow the [template adoption guide](wiki/guides/adopting-template.md) before adding domain behavior.

## Run locally

Docker must be running. Spring Boot detects [compose.yaml](compose.yaml) and starts PostgreSQL 18 when needed.

```bash
./mvnw spring-boot:run
```

Useful endpoints:

- `GET /api/v1/hello` — public shared hello use case.
- `/v3/api-docs` and `/swagger-ui.html` — OpenAPI in non-production profiles.
- `POST /api/v1/ai/generate` — protected; returns 503 until the `openai` profile is enabled.
- `POST /api/v1/storage/uploads` — protected; creates pending metadata and a signed upload URL.
- `POST /api/v1/storage/files/{fileId}/complete` — protected; verifies an owned upload before downloads are enabled.
- `/mcp` — protected and present only when the `mcp` profile is enabled.

## Validate

```bash
./mvnw clean compile
./mvnw test
./mvnw package -DskipTests
docker build -t my-spring-service-blueprint:local .
ai/scripts/wiki-lint.sh
ai/scripts/lint-requirements.sh
```

The full test suite uses Testcontainers PostgreSQL 18 and therefore requires Docker.

## Optional adapters

- Firebase authentication: set `app.authentication.provider=firebase`, `app.firebase.enabled=true`, and mount a readable credential resource through `app.firebase.service-account-key-path`.
- Firebase/Google Cloud Storage: additionally set `app.storage.provider=firebase` and `app.storage.bucket`.
- MCP: activate `mcp`; the stateless endpoint remains protected by the normal security chain.
- OpenAI: activate `openai` and provide `OPENAI_API_KEY`. Automated tests never make provider calls.

Never commit credentials or bake them into an image. See [configuration](wiki/operations/configuration.md) and [security/providers](wiki/security/providers.md).

The Google Cloud deployment workflows are adoption templates and remain skipped until the repository variable `GCP_DEPLOYMENT_ENABLED=true` is set after the target environments are configured.

REST errors use Spring's RFC 9457 `ProblemDetail` contract. Controllers return explicit response records rather than serializing JPA entities; see [API contracts](wiki/guides/api-contracts.md).
