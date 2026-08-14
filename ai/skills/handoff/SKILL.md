---
name: handoff
description: Refresh a requirement workspace so the next agent with fresh context can continue safely. Use when `PLAN.md` or `FINDINGS.md` are stale, sparse, or missing clear pickup guidance.
---

# Handoff

Use this skill to make the active requirement workspace handoff-ready for another agent or for a later session.

The canonical handoff surfaces are `requirements/<slug>/PLAN.md` and `requirements/<slug>/FINDINGS.md`.

## Workflow

1. Identify the active requirement workspace and read `PLAN.md` first, then `FINDINGS.md`.
2. Review only the narrow context needed to describe the current state accurately: recent diffs, validation results, nearby workflow/template files, and the next unfinished checklist item.
3. Update `PLAN.md` so a fresh agent can tell:
   - the goal and current status,
   - what has already been completed,
   - the next concrete action,
   - any blockers, decisions, or validation results that affect continuation,
   - any work that is safe for a parallel agent to pick up independently.
4. Update `FINDINGS.md` with requirement-specific context that should not be rediscovered:
   - relevant wiki/docs/source links,
   - what worked,
   - what did not work or should not be retried blindly,
   - risks, open questions, and useful validation clues.
5. Keep `PLAN.md` focused on control flow and next actions. Keep `FINDINGS.md` focused on reusable requirement-specific context.
6. If durable project knowledge changed, either update the wiki or record a specific wiki follow-up in `PLAN.md`.

## Output Standard

When this skill is used well, a fresh agent should be able to open the requirement workspace and answer:

- What are we trying to accomplish?
- What has already been done?
- What approach is working?
- What approach failed or is suspect?
- What should happen next?
- What can be done in parallel without redoing discovery?

## When To Use It

- before ending a session on an unfinished requirement,
- after meaningful investigation or validation changed the plan,
- when another agent may resume from the same requirement workspace,
- when the current `PLAN.md` or `FINDINGS.md` are too thin to support a safe handoff.