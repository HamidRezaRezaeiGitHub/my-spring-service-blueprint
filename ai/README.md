# AI Agent Pack

This directory contains reusable workflows, scripts, templates, prompts, and metadata for agents in this project.

## Routing

`ai/workflows/workflow-dispatch.md` is the canonical routing table. Read it first to classify the request and pick the smallest set of workflows the task needs. For project orientation, start at `wiki/index.md` when it exists. When resuming work, read the active requirement's `PLAN.md` and `FINDINGS.md` before broad source discovery.

## Common Commands

```sh
ai/scripts/start-requirement.sh "Requirement Title"
ai/scripts/start-requirement.sh --stay-on-current-branch "Requirement Title"
ai/scripts/list-requirements.sh --open
ai/scripts/list-requirements.sh --stats
ai/scripts/lint-requirements.sh
ai/scripts/wiki-lint.sh
ai/scripts/wiki-lint.sh --warn-placeholders
```

## Prompt Templates

- `ai/prompts/session/start-requirement.md`: copy-paste prompt for starting a new requirement and continuing into implementation.
- `ai/prompts/session/start-requirement-planning-only.md`: copy-paste prompt for planning a new requirement without implementation.
- `ai/prompts/session/continue-requirement.md`: copy-paste prompt for resuming an existing requirement workspace.
- `ai/prompts/session/shape-requirement-from-keywords.md`: copy-paste prompt for turning rough keywords or symptoms into a concrete requirement with a stronger interview pass.
- `ai/prompts/session/review-requirement.md`: copy-paste prompt for reviewing a requirement before more work, handoff, commit, or PR.
- `ai/prompts/session/analyze-requirements-stats.md`: copy-paste prompt for summarizing local requirement workspace status and trends.

## Skills

- `ai/skills/interview-questions/SKILL.md`: ask proportional decision-shaping questions before implementation.
- `ai/skills/handoff/SKILL.md`: refresh the active requirement workspace so another agent can continue safely.

## Metadata

`ai/pack.yaml` records the installed pack version.
