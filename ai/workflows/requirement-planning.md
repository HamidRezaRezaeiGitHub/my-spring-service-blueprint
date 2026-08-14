---
name: requirement-planning
use_when: Starting or resuming a standard, large, or risky requirement, feature, bug fix, refactor, investigation, or multi-step documentation task.
---

# Requirement Planning Workflow

Use this workflow at the beginning of any non-trivial requirement, feature, bug fix, refactor, investigation, or multi-step documentation task.
This behavior is a workflow instruction plus automation.

## Trigger

Start or resume a requirement workspace when:

- the user gives a requirement title,
- the user describes a task that will take more than one simple edit,
- switching between agents would benefit from persistent local context,
- planning, investigation, implementation, and validation need to be tracked together.

For tiny one-shot edits, use judgment. Do not create requirement workspaces for trivial commands or purely conversational answers.

Use `ai/workflows/workflow-dispatch.md` for complexity routing and proportional clarifying-question guidance. Record the chosen complexity and any skipped workflows in `PLAN.md`.

## First Agent Responsibilities

- If the user provides a title, use it; otherwise infer a short descriptive title from the prompt.
- Before running the script, evaluate the current branch and working tree, and clarify with the user when the situation is ambiguous:
  - Run `git branch --show-current` and `git status --short` to see the starting state.
  - The script's default behaviour creates or switches to `feature/<slug>` when the current branch is `main`, `master`, or already the expected branch. From any other branch it will switch and warn.
  - If the user is already on a feature/topic branch unrelated to this requirement, ask whether to switch off it, continue this requirement on the existing branch (`--stay-on-current-branch`), or commit/stash first.
  - If the working tree has uncommitted changes, the script will refuse to switch branches. Surface this to the user before running, and confirm whether to commit, stash, or stay on the current branch.
  - If `git status --short` shows untracked files and the script would switch branches, decide whether they should be committed, stashed, removed, ignored, or intentionally carried before proceeding. Untracked files do not usually block `git switch`, but they can confuse handoff and validation.
  - Skip these prompts when the current branch is `main`/`master` with a clean tree, since the default behaviour is unambiguous there.
- Run the script:
  ```sh
  ai/scripts/start-requirement.sh "Requirement Title"
  ```
- Here are the script behaviors:
  - Converts the title to a slug, i.e. lowercase, spaces to hyphens, remove punctuation except hyphens, and collapse repeated hyphens.
  - Creates or reuses `requirements/<slug>/` as the shared local workspace for this requirement. The script keeps `requirements/` ignored by default so PLAN.md and FINDINGS.md stay local, while agents on the same machine can collaborate through the same files.
  - If collaboration across multiple machines is needed, ask the user before intentionally sharing a single requirement folder in git; because `requirements/` is ignored by default, that share should be an explicit force-add of just the chosen folder.
  - Creates or updates `requirements/<slug>/PLAN.md` and `requirements/<slug>/FINDINGS.md` from templates.
  - Creates or switches to the expected branch, preferring `feature/<slug>` when possible.
  - Aborts before switching when the working tree is dirty, and warns when switching from a non-main feature/topic branch.
  - Warns before switching when untracked files are present, but does not fail solely because of untracked files.
- After the script runs, read its output. If it warned about a branch switch, confirm the chosen branch is still appropriate before continuing.
- Each non-quick requirement should have a local workspace, and implementation work should not happen directly on `main` or `master`.
- If you observe that the requirement workspace already exists, or the script output indicates it was reused, follow the resuming responsibilities below.
- Take a look at the generated `PLAN.md` template.
- Record complexity using `ai/workflows/workflow-dispatch.md`.
- Detect whether the prompt needs vibe-coding translation using `ai/workflows/vibe-coding-translation.md`.
- Read project orientation, related wiki pages, and source code only when needed for context. Follow `Research And Context Optimization` below.

## Resuming Agent Responsibilities

- Read `requirements/<slug>/PLAN.md` first.
- Read `requirements/<slug>/FINDINGS.md` before wiki, docs, or source discovery.
- Check the current branch against the expected branch recorded in `PLAN.md`.
- Run `git status --short --branch` and inspect relevant uncommitted changes before editing.
- You may run the same requirement command again to re-check branch and file setup, but use `--stay-on-current-branch` to avoid an unsafe branch switch:
  ```sh
  ai/scripts/start-requirement.sh --stay-on-current-branch "Requirement Title"
  ```
- If the branch or files are not set up as expected, investigate and fix the issue before proceeding. Do not continue without a clear workspace setup.
- Read recent diffs, commits, handoff notes, and the next unfinished checklist item before broad source search.
- Avoid redoing completed research or implementation unless the plan says it is stale, blocked, or suspect.
- Update the plan status and `last_modified` metadata before substantial follow-on work.
- Update `FINDINGS.md` when new reusable context is discovered.
- Append decisions, blockers, validation results, review notes, and handoff notes as work progresses.

Treat this as a resume-session checklist, not as permission to re-run full requirement planning from scratch. If the existing plan is too sparse or stale to guide safe work, refresh only the missing sections and explain why.

## All Agents Responsibilities

- Identify and run a safe, reasonably scoped baseline verification. Follow `Baseline Verification At Start` below.
- Ask proportional clarifying questions only for decisions that remain unresolved after the context pass. Follow `ai/skills/interview-questions/SKILL.md`.
- Treat `requirements/<slug>/PLAN.md` and `FINDINGS.md` as live handoff artifacts. Another agent may resume the requirement, or pick up a parallel-safe task, at any time.
- Update `FINDINGS.md` with reusable context, links, user clarifications, risks, validation clues, and discoveries that future agents should not have to rediscover.
- Fill in the useful parts of `PLAN.md`: request summary, complexity, workflows used or skipped, scope, assumptions, baseline result, implementation steps, validation strategy, decisions, and handoff notes.
- If the handoff state becomes stale or sparse, use `ai/skills/handoff/SKILL.md` to normalize the requirement workspace before stopping or before resuming after a gap.
- If the request needs vibe-coding translation, record the concrete translation, acceptance criteria, assumptions, and remaining open questions in `PLAN.md`.
- If the request affects boundaries, contracts, data flow, integrations, persistence, public APIs, cross-cutting concerns, security, privacy, or long-term maintainability, consult `ai/workflows/architecture.md` and record the architecture impact in `PLAN.md`.
- If the request edits a `README.md`, user-flow document, or interactive HTTP testing artifact, record that documentation/testing impact in `PLAN.md` so the affected surfaces stay explicit.
- Before substantial implementation, update the plan status so another agent can tell whether the work is planning, active, validating, reviewing, blocked, parked, complete, or cancelled.
- Keep detailed reusable context in `FINDINGS.md`; keep `PLAN.md` focused on current status and next actions.

## Research And Context Optimization

- Start from `PLAN.md` and then `FINDINGS.md` before scanning source code.
- When resuming, identify the next unfinished checklist item or handoff note before opening broad source areas.
- Use `FINDINGS.md` as the requirement-specific context cache and first filter for wiki/source lookup.
- Translate non-technical or vibe-style requests before broad source search.
- Read `wiki/index.md` and relevant wiki pages when they exist and the plan, findings, or task scope says durable project context may matter.
- Consult architecture docs and `ai/workflows/architecture.md` for architecture-sensitive work.
- Read documented architecture, testing, CI, and command references before broad code search.
- Search source code only for the part of the requirement being worked on, or similar code areas identified.
- Update `FINDINGS.md` with discoveries that future agents would otherwise need to rediscover.
- Promote durable discoveries from `FINDINGS.md` into the wiki during the requirement or before finishing, using `ai/workflows/wiki-documentation.md`.
- Keep raw logs and large copied outputs out of `PLAN.md`; summarize and link to files when needed.
- Follow `ai/workflows/command-execution.md` when running terminal commands.

## Baseline Verification At Start

Before source edits for `standard`, `large`, or `risky` implementation work:

1. Look for an existing validation command in `AGENTS.md`, `README`, contribution docs, `wiki/`, CI files, package scripts, build files, or task runners.
2. Choose the smallest meaningful baseline that proves the current workspace starts from a known state. Prefer targeted smoke checks over full suites when full validation is slow or noisy.
3. Use `ai/workflows/command-execution.md` to keep outputs concise.
4. Record the command and result in `PLAN.md`.
5. If the baseline fails before any source edits, document it as pre-existing in `PLAN.md` and add useful details to `FINDINGS.md`.
6. If no baseline can be run locally, record why and what CI or manual check would provide equivalent confidence.

If the requirement is to fix the failing baseline or another observed defect, follow `ai/workflows/systematic-debugging.md` before proposing the fix.

Do not block quick tasks on baseline verification unless the quick task itself is a validation request.

## PLAN.md Expectations

`PLAN.md` should stay useful for a human and for any future agent. Keep it concise but current.

Treat it as a live control surface, not a static summary written only at the end. Another agent should be able to open it and continue safely.

The template is a starting point, not a fixed schema. Agents may add, remove, merge, or rename sections to fit the requirement, as long as Markdown remains readable and any project linting still passes.

For standard to risky requirements, break implementation into phases. Each phase should have a concrete goal, verifiable steps, and a done condition explicit enough to hand off to a developer. Phases make it safe for another agent to pick up where the previous one stopped.

Prefer sections that answer:

- What is the request?
- What complexity and workflows apply?
- What is included, out of scope, assumed, or still open?
- What context was checked, and where is deeper reusable context recorded?
- What baseline verification was run before source edits?
- What is the current plan, status, and next action?
- What progress has already been made?
- What phases, done conditions, and validation checkpoints define the work?
- What worked, what did not work, and what should not be retried blindly?
- What decisions, validation results, review notes, blockers, or handoff notes matter?
- What task slice, if any, is safe for another agent to pick up in parallel?
- What durable project knowledge changed, and was it promoted to the wiki?

## Requirement Status

Use a concise status value in `PLAN.md` metadata. Recommended statuses are:

- `planning`: context gathering or plan shaping is underway.
- `active`: implementation, investigation, or validation is underway.
- `blocked`: work cannot proceed without a decision, dependency, access, or failing baseline resolution.
- `parked`: intentionally paused and expected to resume later.
- `complete`: requirement is handled and validation/review notes are recorded.
- `cancelled`: requirement will not continue.

These statuses work with `ai/scripts/list-requirements.sh` and `ai/scripts/lint-requirements.sh`. The scripts also map common status variants into conceptual categories with case-insensitive substring matching, so values such as `completed`, `done`, `finished`, or `implemented` are treated as done.

## Quality Planning

For implementation requirements, the plan should identify the likely validation surface before coding:

- Vibe-coding translation needs, using `ai/workflows/vibe-coding-translation.md`.
- Architecture impact, using `ai/workflows/architecture.md`.
- Relevant tests or test areas, using `ai/workflows/testing-quality.md`.
- README impact and any root-README updates needed.
- User-flow documentation impact, using `ai/workflows/user-flow-docs.md` when `src/main/resources/user-flows/` changes.
- Interactive API walkthrough needs, using `ai/workflows/http-testing.md` when local endpoint sessions or response samples are part of the requirement.
- Debugging approach for bugs, failing tests, regressions, or unexpected behavior, using `ai/workflows/systematic-debugging.md`.
- CI expectations, using `ai/workflows/ci-validation.md`.
- Final self-review or PR-review needs, using `ai/workflows/code-review.md`.
- Wiki pages that may need updates, using `ai/workflows/wiki-documentation.md`.

If a project lacks explicit commands, inspect CI configuration, package scripts, build files, and wiki/docs before deciding what to run.

## FINDINGS.md Expectations

`FINDINGS.md` should capture requirement-specific knowledge that is useful but too detailed for `PLAN.md`.

Treat it as the shared requirement context cache for future agents, not as a private scratchpad.

Use it for:

- relevant wiki/docs links and why they matter,
- relevant source files, classes, methods, tests, configs, or commands,
- user clarifications and back-and-forth decisions,
- investigation notes,
- what worked,
- what did not work or should not be retried blindly,
- risks and open questions,
- validation clues.

Do not duplicate stable project knowledge from the wiki. Link to it and summarize the requirement-specific relevance.

## Wiki Maintenance During Requirements

Use `ai/workflows/wiki-documentation.md` throughout the requirement lifecycle:

1. At discovery time, read relevant wiki pages first and capture tentative or task-specific discoveries in `FINDINGS.md`.
2. During implementation and validation, note durable project knowledge that future agents should not rediscover.
3. Before finalizing, decide whether that durable knowledge belongs in existing wiki pages, a new page, or only in `FINDINGS.md`.
4. When the wiki changes, update affected pages, `wiki/index.md`, `wiki/log.md`, and run `ai/scripts/wiki-lint.sh` when available.

If no wiki update is needed, record the reason briefly in `PLAN.md`.
