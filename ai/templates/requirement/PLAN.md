---
title: "{{TITLE}}"
slug: "{{SLUG}}"
expected_branch: "{{BRANCH}}"
created: "{{CREATED_AT}}"
last_modified: "{{CREATED_AT}}"
status: planning
---

# Requirement Plan: {{TITLE}}

This plan is a working record, not a fixed form. Keep the useful sections, remove irrelevant TODOs, and add sections such as architecture, test design, wiki maintenance, rollout, or risk review when the requirement needs them, as long as project linting still passes.

Treat it as a live handoff artifact: another agent may resume from it, or pick up a safe parallel task, at any time.

Status examples: `planning`, `active`, `blocked`, `parked`, `complete`, `cancelled`.

## Request

TODO: Summarize the user's requirement in a few sentences.

## Routing

- Level: TODO `quick`, `standard`, `large`, or `risky`.
- Workflows: TODO: List only the workflows this task needs, such as `requirement-planning`, `architecture`, `testing-quality`, `user-flow-docs`, `http-testing`, `wiki-documentation`, `ci-validation`, or `code-review`.
- Skipped: TODO: Note intentionally skipped workflows or escalation triggers.

## Scope

- Included: TODO.
- Out of scope: TODO.
- Assumptions or open questions: TODO.

## Baseline Verification

- Needed: TODO `yes` or `no`.
- Command or check: TODO.
- Result before source edits: TODO `passed`, `failed`, `not run`, or `not available`.
- Notes: TODO.

## Plan

Break implementation into phases. Each phase should be independently completable and verifiable — small enough to hand off to a developer. Phase 0 and the closing phases are fixed; replace the middle phases with actual implementation phases.

### Phase 0: Setup

- [ ] Confirm or infer requirement title.
- [ ] Classify complexity.
- [ ] Vibe-coding translation, if needed. (Based on `ai/workflows/vibe-coding-translation.md`)
- [ ] Context discovery, including [FINDINGS.md](./FINDINGS.md) and relevant wiki/docs/source. (Based on `ai/workflows/requirement-planning.md` and `ai/workflows/wiki-documentation.md`)
- [ ] Baseline verification before source edits.
- [ ] Clarifying and suggestion decision-shaping questions. (Based on `ai/skills/interview-questions/SKILL.md`)
- [ ] Update [FINDINGS.md](./FINDINGS.md), fill in phases below.

**Done when:** plan context is clear, phases are defined, and baseline is recorded.

### Phase 1: TODO — [brief scope]

**Goal:** TODO: one sentence describing what this phase delivers.

- [ ] TODO
- [ ] TODO

**Done when:** TODO: verifiable condition (test passes, command succeeds, output exists, etc.).

### Phase 2: TODO — [brief scope]

**Goal:** TODO.

- [ ] TODO

**Done when:** TODO.

### Validation

**Goal:** Confirm the implementation meets requirements.

- [ ] Run targeted validation and review CI expectations based on `ai/workflows/ci-validation.md`.
- [ ] Perform two-pass self-review based on `ai/workflows/code-review.md`.

**Done when:** CI passes and self-review complete.

### Wrap-up

**Goal:** Close the requirement cleanly.

- [ ] Update wiki and handoff notes when durable knowledge changes.
- [ ] Update `last_modified` and `status` in this plan.

**Done when:** Wiki updated or reason noted in PLAN.md; handoff notes current.

## Decisions

- TODO: Record decisions and rationale.

## Validation

- TODO: Record commands run and results.

## Handoff Notes

(Based on `ai/skills/handoff/SKILL.md`)

- What worked: TODO.
- What did not work or should not be retried blindly: TODO.
- Parallel-safe pickup tasks, if any: TODO.
- Keep detailed reusable context in [FINDINGS.md](./FINDINGS.md).
