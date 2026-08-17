---
title: Adopting the Template
domain: guides
tags: [template, setup, customization]
status: current
last_updated: 2026-08-17
---

# Adopting the Template

Create the target repository from this template, then make one coherent naming pass before adding domain behavior:

1. Replace Maven coordinates and the `com.example.application` package.
2. Replace application, database, Compose volume, container-image, MCP-server, OpenAPI-title, and cloud-service names.
3. Choose the target repository's license; this blueprint intentionally does not make that legal decision for adopters.
4. Keep PostgreSQL/Flyway/Hibernate validation aligned and add the target's next versioned migration rather than runtime schema generation.
5. Keep only the provider adapters the target needs. The blueprint ships one provider-neutral artifact containing all demonstrations for one-clone usability; adopters prioritizing a smaller artifact can remove unused dependencies or split adapters into modules.
6. Extend the minimal `Account` only from real product requirements. `displayName`, role, and active status are sufficient for the generic authentication example; email remains a provider-identity claim because it can be absent or change.
7. Replace the hello example when a real feature demonstrates REST/MCP parity, and keep API DTOs separate from JPA entities.
8. Leave the GCP deployment templates disabled until the cloud resources and GitHub Environments exist; then set the repository variable `GCP_DEPLOYMENT_ENABLED=true` after reviewing the target's deployment policy.

Run the full validation sequence from the root [README](../../README.md) after renaming. Automated architecture tests protect the controller-to-service and provider-boundary rules during customization.
