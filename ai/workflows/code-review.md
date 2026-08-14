---
name: code-review
use_when: Reviewing a pull request, reviewing another agent's changes, or performing final self-review before marking source work complete.
---

# Code Review Workflow

Use this workflow when reviewing a pull request, reviewing another agent's changes, or performing a final self-review before marking source work complete.

## Goal

Find meaningful defects and risks before the change is handed off, committed, merged, or released. Review behavior, architecture, tests, CI, security, documentation, and maintainability before style.

This workflow follows the same SOP-based idea used elsewhere in this pack: make intermediate review assumptions explicit so specialized or sequential agents can coordinate without losing context.

Use two separate review passes. This borrows the useful part of subagent-driven review without requiring a specific agent platform:

- Pass 1 checks whether the change satisfies the requirement and avoids scope drift.
- Pass 2 checks whether the implementation is safe, maintainable, tested, and ready to ship.

## When To Run

Run code review:

- after implementation and validation work are complete,
- before finalizing the source-code portion of a requirement,
- before committing or opening a PR,
- when asked to review a PR or diff,
- after responding to review feedback, to verify the fix.

For tiny documentation-only changes, use a lighter pass focused on correctness and links.

## Inputs

Start from:

- `requirements/<slug>/PLAN.md` when available,
- `requirements/<slug>/FINDINGS.md` when available,
- PR description, issue, or user request,
- changed-file list and diff,
- relevant wiki pages,
- architecture, testing, and CI workflows,
- validation results already recorded.

If the review is large, review in focused chunks. Prefer reviewing smaller diffs over trying to absorb a huge change at once.

## Author Self-Review

Before asking someone else to review or before finalizing:

1. Run Pass 1: requirement fit.
2. Run Pass 2: implementation quality and risk.
3. Remove accidental files, debug code, dead code, and unrelated changes.
4. Add notes for reviewers when file order, risky areas, or non-obvious decisions matter.
5. Verify tests, CI expectations, and wiki updates are recorded in the plan.
6. Verify important review findings are captured in `FINDINGS.md` or the PR review when they help future agents.
7. If the review causes source changes, rerun the smallest meaningful validation affected by those changes.

## Two-Pass Review

Keep the passes distinct even when one person or one agent performs both. If using multiple agents, ask one reviewer to focus on Pass 1 and another to focus on Pass 2.

### Pass 1: Requirement Fit

Ask:

- Does the change solve the requested problem and acceptance criteria?
- Did the implementation follow the current `PLAN.md` decisions?
- Is any behavior missing, overbuilt, or outside scope?
- Are user-facing flows, API behavior, data changes, and edge cases aligned with the requirement?
- Were open questions or assumptions resolved, documented, or left visibly pending?
- Do docs or wiki updates match what actually changed?

Stop and fix blocking requirement-fit issues before spending time on deeper implementation polish.

### Pass 2: Implementation Quality And Risk

Ask:

- Is the code in the right layer/module and consistent with existing patterns?
- Are architecture boundaries, contracts, data flow, and integrations preserved?
- Are failure modes, permissions, privacy, security, performance, and operations risks handled?
- Do tests cover meaningful behavior and likely regressions?
- Were tests weakened, skipped, or changed without a clear reason?
- Are CI/build, generated files, dependencies, and lockfiles intentional?
- Is the change maintainable, local, and understandable without unnecessary abstraction?

If Pass 1 fails, record that first. Do not hide requirement mismatch behind style or refactor comments.

## Detailed Review Order

Within the two passes, review in this order:

1. Pass 1 requirement fit: does the change solve the requested problem without scope creep?
2. Pass 1 docs/wiki fit: did durable project knowledge change and is it represented accurately?
3. Pass 2 architecture: does it respect boundaries, ownership, data flow, and contracts?
4. Pass 2 behavior: are edge cases, failure modes, permissions, and user impact correct?
5. Pass 2 tests: do tests catch real bugs and regressions?
6. Pass 2 CI/build: would configured checks pass?
7. Pass 2 security/privacy: are secrets, auth, data exposure, injection, dependency, and permission risks addressed?
8. Pass 2 operations: are config, migration, observability, performance, and deployment implications handled?
9. Pass 2 maintainability: is the code simple, local, readable, and consistent with existing patterns?

Style and naming matter, but do not let nitpicks crowd out correctness and risk.

## What To Look For

- Missing or incorrect requirement behavior.
- Broken public contracts, APIs, schemas, or data migrations.
- Logic in the wrong layer or module.
- Hidden coupling or duplicated abstractions.
- Edge cases not handled.
- Error paths, empty states, retries, or timeouts missing.
- Authorization, privacy, or data integrity gaps.
- Tests that only prove mocks/frameworks work.
- Tests that were weakened, skipped, or updated without rationale.
- CI, formatting, typecheck, or build failures.
- Dependency or lockfile changes without justification.
- Wiki/docs drift after architecture, behavior, testing, or operations changes.

## Feedback Style

When reviewing for another agent or human:

- Lead with blocking correctness, security, or data-loss issues.
- Be specific: file, line, observed problem, expected behavior, and suggested fix when useful.
- Distinguish blockers from suggestions.
- Avoid repeated nitpicks; summarize repeated patterns once.
- Do not act like a human linter. Prefer issues that affect behavior, risk, maintainability, or clarity.
- If no issues are found, say what was reviewed and mention residual risk or test gaps.

## PR Review Output

For PR review, structure the response as:

1. Findings, ordered by severity.
2. Open questions or assumptions.
3. Validation reviewed or missing.
4. Two-pass coverage: note whether Pass 1 requirement fit and Pass 2 implementation quality were both reviewed.
5. Brief summary only after findings.

For self-review before finalizing, record both pass results in `PLAN.md`.

## Validation

Use:

- `ai/workflows/testing-quality.md`,
- `ai/workflows/ci-validation.md`,
- `ai/workflows/architecture.md`,
- `ai/workflows/wiki-documentation.md`,
- `ai/workflows/command-execution.md`.

If validation cannot be run, record why and identify what CI or a human should verify.
