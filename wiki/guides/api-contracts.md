---
title: API Contracts
domain: guides
tags: [api, dto, errors, openapi]
status: current
last_updated: 2026-08-17
---

# API Contracts

Controllers expose dedicated request and response records rather than JPA entities. A response record is worthwhile even when its current mapping is short: it prevents persistence annotations, lazy relationships, internal fields, and future entity changes from silently changing the public contract. Do not create DTOs that merely mirror shared entity base classes; model only concrete request or response contracts.

HTTP failures use Spring Framework's RFC 9457 `ProblemDetail` representation with `application/problem+json`. Framework exceptions are handled through `ResponseEntityExceptionHandler`, which preserves correct statuses such as 405 and 415. Validation problems add an `errors` array as an RFC 9457 extension. Feature-specific exception advice stays beside its feature so the global error package does not depend on every feature and application services remain transport-neutral.

Protected controllers declare the `bearerAuth` OpenAPI security requirement. Public operations such as hello remain explicitly public through the security chain.

The generated `/v3/api-docs` contract is regression-tested as the source of truth. Every published operation has a human-facing tag, summary, description, documented media types, relevant success/error statuses, and described path parameters. Every published application schema and property has a description; fields include requiredness, formats, bounds, enum references, and examples where useful. Internal provider values and JPA entities must not leak into `components.schemas`.
