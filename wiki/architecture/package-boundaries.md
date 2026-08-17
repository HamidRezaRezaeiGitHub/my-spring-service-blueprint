---
title: Package Boundaries
domain: architecture
tags: [packages, boundaries, feature-first]
status: current
last_updated: 2026-08-17
---

# Package Boundaries

Top-level feature packages are `account`, `authentication`, `hello`, `ai`, and `storage`. They may contain narrow adapter or API DTO subpackages. Resource rules such as `AccountAuthorization` and feature-specific HTTP exception mappings stay with the resource owner.

Cross-cutting packages own one reusable contract: `api`, `authorization`, `entity`, `error`, `filtering`, `firebase`, `mapping`, `observability`, `pagination`, `security`, `utility`, and `validation`. `entity` contains only shared mapped base types; concrete JPA entities remain feature-owned. These packages are not miscellaneous dependency buckets.

Dependency direction is inbound adapter → feature service → persistence/provider boundary → optional outbound adapter. Transactions and authorization are applied at service or method-security boundaries so transport choice cannot change business behavior.

ArchUnit tests enforce that controllers do not depend directly on repositories, feature services do not depend on concrete provider adapters, and the shared `entity` package does not own concrete entities. JSpecify `@NullMarked` is repeated in each subpackage because package annotations do not cascade.
