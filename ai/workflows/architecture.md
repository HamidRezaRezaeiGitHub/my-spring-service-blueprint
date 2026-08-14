---
name: architecture
use_when: Planning, implementing, reviewing, or answering questions that affect structure, boundaries, data flow, integrations, or long-term maintainability.
---

# Architecture Workflow

Use this workflow when planning, implementing, reviewing, or answering questions that may affect the project's structure, boundaries, data flow, integration points, or long-term maintainability.

## Goal

Maintain an accurate mental model of the current system before making changes. Architecture guidance should reduce accidental coupling, misplaced logic, duplicated patterns, and documentation drift.

## When To Consult

Consult the architecture workflow when a requirement touches:

- module/package boundaries,
- public APIs or contracts,
- data models or persistence,
- cross-cutting concerns such as auth, logging, configuration, validation, caching, errors, observability, or localization,
- infrastructure, deployment, CI, or runtime topology,
- shared libraries or common components,
- performance, scalability, reliability, security, or privacy,
- a new dependency or major refactor.

For tiny isolated edits, a quick pattern check is enough.

## Context Lookup

Before planning implementation:

1. Start with `wiki/index.md` when it exists.
2. Read relevant architecture pages such as:
   - `wiki/architecture/system-overview.md`
   - `wiki/architecture/tech-stack.md`
   - `wiki/architecture/data-flow.md`
   - `wiki/architecture/integration-points.md`
   - `wiki/architecture/decisions.md`
3. Read domain, operations, testing, or CI pages relevant to the change.
4. Inspect only the source areas needed to verify current patterns.

If architecture docs are missing or stale, update the requirement plan with the gap and create or update wiki pages when the requirement affects architecture.

## Architecture Checkpoints

While planning and implementing, answer these briefly in `PLAN.md` when relevant:

- What existing boundary or layer owns this behavior?
- What source files or modules are the likely change area?
- What interfaces, APIs, schemas, or contracts are affected?
- What data flow changes from input to storage/output?
- What cross-cutting concerns apply?
- What existing pattern should be followed?
- What alternatives were considered and rejected?
- What risks need testing or review?

## Implementation Guidance

- Prefer existing architecture and local patterns over new structure.
- Keep business/domain behavior in the layer that already owns it.
- Avoid adding dependencies unless they clearly reduce risk or complexity.
- Avoid broad refactors unless the requirement truly needs them.
- Preserve public contracts unless the requirement explicitly changes them.
- If changing a boundary, document the reason and update affected wiki pages.

## Documentation

Architecture documentation belongs in the wiki. Use focused pages instead of one giant architecture document.

Use the same page structure listed in `Context Lookup` above. For major decisions, record the decision, context, considered options, outcome, and consequences.

## Review Lens

When reviewing architecture impact, look for:

- logic in the wrong layer,
- duplicated abstractions,
- missing boundary tests,
- broken data ownership,
- hidden coupling,
- undocumented public contract changes,
- operational or security implications,
- wiki drift.
