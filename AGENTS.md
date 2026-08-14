# AGENTS.md

## Project Overview

This repository is a reusable Java 25 Spring Boot 4 backend template. It is a modular monolith with feature-first packages, PostgreSQL 18 in every environment, a Flyway-managed schema, credential-free defaults, optional Firebase and OpenAI adapters, and a project wiki that serves as the durable orientation layer for humans and agents.

## Repository Map

- `src/main/java/com/example/application/` - feature packages plus narrow cross-cutting capability packages; there is no miscellaneous common layer.
- `src/test/java/com/example/application/` - unit, PostgreSQL-backed JPA, security, REST, MCP, and provider-adapter tests.
- `wiki/` - persistent project knowledge: architecture, domain, operations, and guides.
- `.github/instructions/` - thin auto-discovery wrappers for Copilot that point to the canonical `ai/workflows/` and `ai/skills/` files.
- `ai/` - shared agent workflows, requirement templates, wiki templates, and helper scripts from the adopted instruction pack.
- `requirements/` - local-only planning and findings workspaces for non-trivial tasks.
- `.github/workflows/` - CI and security automation.

## Commands

- `./mvnw clean compile | grep -E "(BUILD SUCCESS|BUILD FAILURE)"` - compile baseline. Verified 2026-08-13.
- `./mvnw test | grep -E "(Tests run:|BUILD SUCCESS|BUILD FAILURE)"` - full test suite. Docker is required because Spring-context tests use Testcontainers Postgres.
- `./mvnw package -DskipTests | grep -E "(BUILD SUCCESS|BUILD FAILURE)"` - package the app after tests have run.
- `./mvnw spring-boot:run` - run the backend locally; Spring Boot can auto-start the local Postgres compose service.
- `ai/scripts/start-requirement.sh "Requirement Title"` - create or resume a requirement workspace.
- `ai/scripts/lint-requirements.sh` - validate requirement workspace metadata and structure.
- `ai/scripts/wiki-lint.sh` - lint wiki links, frontmatter, and index coverage.
- `ai/scripts/audit-adoption.sh .` - compare the adopted pack files against the source pack.

There is no separate install, lint, or typecheck command beyond the Maven lifecycle used by this repository.

## Work Rules

- Before acting on a new request, route it with `ai/workflows/workflow-dispatch.md` and use only the workflows that matter for the task.
- For project orientation, start with `wiki/index.md`, then read the relevant wiki pages, root `README.md`, and the nearest owning source packages or `package-info.java` files before broad source search.
- For non-trivial work, use local `requirements/<slug>/PLAN.md` and `FINDINGS.md` to capture routing, decisions, validation, and reusable discoveries.
- Treat `ai/workflows/*.md` and `ai/skills/**/SKILL.md` as the source of truth for reusable agent behavior. Keep `.github/instructions/*.md`, `.claude/skills/*/SKILL.md`, and `.gemini/skills/*/SKILL.md` as thin discovery layers that point to those canonical files.
- Keep changes scoped to the requirement and follow existing package, testing, and documentation patterns before inventing new structure.
- Update the root `README.md`, the wiki, and only the smallest necessary local source docs when durable project knowledge, file structure, or operating guidance changes.
- Do not add dependencies, modify CI, or change public behavior unless the requirement calls for it.
- Do not commit secrets, machine-specific paths, temporary clones, or local credential files.
- Run the smallest meaningful validation before finishing, then widen only when the touched surface requires it.
