---
name: http-testing
use_when: Interactively testing blueprint REST or MCP endpoints against a locally running server.
---

# HTTP Testing Workflow

Use this workflow when interactively exercising the blueprint's HTTP endpoints against a real local server.

## Context First

Before making calls:

1. Read `wiki/index.md`.
2. Read the relevant architecture, provider, or MCP/AI wiki page.
3. Inspect the owning controller or MCP adapter and its integration test.
4. Use checked-in HTTP examples only when the repository has added them for the endpoint.

## Preconditions

- The Spring Boot app must be running locally, typically on `http://localhost:8080`.
- When authentication is involved, use an explicitly configured test provider and never place tokens in committed files.

## Reference Files

Use the owning source, integration tests, generated OpenAPI, and wiki as the request contract. Private HTTP-client environment files remain local and uncommitted.

## Interactive Session Rules

When running a multi-step endpoint walkthrough:

1. Confirm the app is reachable before calling business endpoints.
2. Pretty-print or summarize responses for review.
3. Pause after each call when the user requested an interactive walkthrough.
5. If a response is large, summarize counts, structure, or key fields rather than dumping everything.
5. Never save Bearer tokens, signed URLs, API keys, or credential-bearing query strings.
6. If a call fails, surface the full failure clearly and explain the next likely debugging step.

## Authentication Flow

For auth-protected sessions:

- obtain or refresh a token through the configured external provider,
- verify the token with the account `me` endpoint,
- re-authenticate if a session begins returning 401 responses because of token expiry.

## Output Hygiene

- Use sequential, descriptive filenames.
- Keep sensitive values out of committed files.
- Prefer saving representative sample responses rather than noisy duplicates.

## Validation

After an HTTP testing session:

- confirm any intentionally saved response samples reflect the actual calls made and contain no secrets,
- summarize which endpoints were exercised and which were not,
- note any durable API or auth discoveries in the relevant wiki page or active requirement findings.
