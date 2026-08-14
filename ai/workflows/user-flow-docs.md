---
name: user-flow-docs
use_when: Creating or updating user flow documentation under src/main/resources/user-flows/.
---

# User Flow Documentation Workflow

Use this workflow when documenting or updating user-facing flows under `src/main/resources/user-flows/`.

## Context First

Before editing a flow document:

1. Read `wiki/index.md`.
2. Read relevant domain and architecture wiki pages.
3. Read the root `README.md` and nearby owning source packages or `package-info.java` files to identify the owning controllers, services, DTOs, and repositories.
4. Read the current flow file and `src/main/resources/user-flows/index.md` before changing structure.

## User-Flow Layout

User flow docs live under `src/main/resources/user-flows/`.

- `index.md` is the central table of contents.
- Flow files follow the `user-flows-<category>.md` naming pattern.
- Reuse an existing feature category when one fits; do not add template-specific sample domains.

## Supported Tasks

Use this workflow for two kinds of work:

- filling in or correcting a documented flow that already exists in the codebase,
- drafting or adding a new flow document for behavior that is planned or newly implemented.

## Workflow

### Existing Flows

1. Find the flow in `src/main/resources/user-flows/index.md`.
2. Read the corresponding `user-flows-*.md` file and locate the relevant `##` section.
3. Trace the implementation through the owning controllers, services, DTOs, and repositories.
4. Update the flow text and table of contents as needed.

### New Flows

1. Confirm the flow is not already documented.
2. Choose the correct `user-flows-*.md` file or create a new category file when needed.
3. Inspect similar flows and the implementation areas that will own the behavior.
4. Add the new flow section and update both the local file TOC and `index.md`.

## Required Flow Structure

Each flow section should use this shape:

- `## <Flow Name>`
- `### Description`
- `### Preconditions`
- `### User Actions`
- `### System Behavior`
- `### Result`
- `### API Endpoints`
- `### Gaps / TODO` when applicable

## Style Rules

- Write in present tense.
- Use business and UX language first; keep technical details concise.
- Use `User` and `System`, not `we`.
- Link relevant implementation files inline from `System Behavior` and `API Endpoints`.
- Preserve the existing file ordering and formatting unless a clear cleanup is needed.
- Keep the table of contents current in both the flow file and `index.md`.

## Validation

After editing user-flow docs:

- check that the flow is linked from `src/main/resources/user-flows/index.md`,
- verify relative links to Java files and sibling flow documents,
- update the wiki when the user flow reveals durable project behavior not already documented elsewhere.
