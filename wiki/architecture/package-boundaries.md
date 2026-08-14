---
title: Package Boundaries
domain: architecture
tags: [packages, boundaries, feature-first]
status: current
last_updated: 2026-08-13
---

# Package Boundaries

Top-level feature packages are `account`, `authentication`, `hello`, `ai`, and `storage`. They may contain narrow adapter or DTO subpackages. Resource rules such as `AccountAuthorization` stay with the resource owner.

Cross-cutting packages own one reusable contract: `api`, `authorization`, `error`, `filtering`, `firebase`, `mapping`, `observability`, `pagination`, `persistence`, `security`, `utility`, and `validation`. They are not miscellaneous dependency buckets.

Dependency direction is inbound adapter → feature service → persistence/provider boundary → optional outbound adapter. Transactions and authorization are applied at service or method-security boundaries so transport choice cannot change business behavior.
