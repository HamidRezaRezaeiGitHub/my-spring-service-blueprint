---
title: Security and Providers
domain: architecture
tags: [security, firebase, storage, authentication]
status: current
last_updated: 2026-08-17
---

# Security and Providers

HTTP security is stateless. `AuthenticationFilter` verifies a Bearer token through `ExternalAuthenticationService`, maps its verified provider subject through `user_authentications`, and installs a local account principal. Tokens are never retained or logged.

Security is not runtime-disableable. Tests authenticate explicitly instead of weakening the production filter chain. Registration bypasses only filter-level local-account lookup because it must verify a new external identity inside the registration use case. The provider subject (`sub`, or Firebase UID) is the stable identity key; email is optional provider metadata and is never used as identity.

The default `noop` authentication adapter always rejects tokens. Firebase authentication requires both `app.authentication.provider=firebase` and shared Firebase SDK configuration. Missing credentials under explicit Firebase configuration fail startup instead of falling back.

Storage follows the same policy. `NoOpStorageProvider` throws for every operation. The Firebase adapter creates bounded signed URLs against the configured bucket. Request logging excludes headers and query strings so Bearer tokens, signed URLs, API keys, and sensitive parameters do not enter application logs.

A signed upload begins as pending metadata. The owner calls `POST /api/v1/storage/files/{fileId}/complete`; the service verifies that the provider object exists before marking it downloadable. Downloads require both the completed state and current provider existence.
