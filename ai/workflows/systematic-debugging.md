---
name: systematic-debugging
use_when: Debugging bugs, failing tests, build failures, performance regressions, flaky behavior, or unexpected behavior before proposing fixes.
---

# Systematic Debugging Workflow

Use this workflow when the task is to diagnose or fix a bug, failing check, flaky test, build break, performance regression, or unexpected behavior.

The goal is to find and fix the root cause without thrashing. Evidence comes before fixes; the size of the evidence loop should still match the size and risk of the problem.

## Proportionality

For quick and obvious bugs, keep the loop lightweight:

1. read the exact error or symptom,
2. reproduce or identify why reproduction is not available,
3. inspect the directly relevant code or recent change,
4. make the smallest root-cause fix,
5. run the narrowest meaningful verification.

For ambiguous, repeated, cross-component, flaky, production, or high-impact issues, use the full workflow below and record the investigation in `PLAN.md` or `FINDINGS.md`.

## Phase 1: Capture And Reproduce

Before proposing a fix:

1. Read the full error, warning, stack trace, failing assertion, or user-visible symptom.
2. Record the exact command, input, route, screen, environment, or steps that show the issue.
3. Reproduce the problem locally when feasible.
4. If it cannot be reproduced, gather the missing evidence first: logs, CI output, screenshots, request payloads, timing details, environment differences, or user steps.
5. Check relevant uncommitted changes and recent commits for likely causes.

Success means you can state what fails, where it fails, and how you know.

## Phase 2: Localize The Cause

Narrow the failing area before changing code:

1. Identify the smallest failing command, test, route, component, or input.
2. Trace bad data backward from the failure to its source instead of patching the symptom.
3. Compare broken behavior with a nearby working example or established pattern.
4. For multi-component systems, inspect each boundary:
   - what data enters,
   - what data exits,
   - which config or environment values are present,
   - which layer first shows the bad state.
5. Add temporary diagnostics only when they answer a specific question, and remove them before finishing unless they become intentional observability.

Success means you can identify the likely root cause and the boundary that owns the fix.

## Phase 3: Test A Hypothesis

Use one hypothesis at a time:

1. State the hypothesis clearly: "I think X is happening because Y."
2. Choose the smallest check that can confirm or disprove it.
3. Avoid bundling unrelated fixes, refactors, or cleanup into the hypothesis test.
4. If the hypothesis is wrong, update the notes and form a new one from the evidence.

If three reasonable fix attempts fail or each fix exposes a different shared-state, coupling, or architecture problem, stop and reassess the design with the user before continuing.

## Phase 4: Fix And Verify

Fix the root cause, then prove it:

1. Add or update a regression test when the repo has a meaningful test surface for the bug.
2. If an automated test is not practical, record the manual or one-off check that demonstrates the failure before and success after.
3. Implement the smallest fix at the source of the problem.
4. Run the narrow verification first, then broader validation when the touched surface is shared.
5. Use `testing-quality.md` for test changes, `ci-validation.md` before finishing, and `code-review.md` for final review.

Success means the original reproduction no longer fails, related behavior still works, and the verification is recorded.

## Recording

For standard, large, or risky debugging work, record in `PLAN.md` or `FINDINGS.md`:

- the reproduction command or steps,
- the evidence that localized the cause,
- the confirmed hypothesis,
- the fix scope,
- the regression test or manual verification,
- any remaining risk or follow-up.

Keep raw logs out of the plan. Summarize the meaningful lines and link to stable files when useful.

## Red Flags

Stop and return to evidence gathering when you notice:

- proposing fixes before reading the error or reproducing the issue,
- changing multiple variables at once,
- weakening or skipping a failing test without understanding it,
- patching the deepest stack frame instead of tracing the bad value to its source,
- keeping temporary diagnostics, sleeps, retries, or broad exception handling as a substitute for root cause,
- claiming a fix without rerunning the original reproduction.
