# Claude Code Project Memory

Follow `AGENTS.md` at the repository root for project-wide instructions.

Project reusable behavior lives under `ai/workflows/` and `ai/skills/`. The files under `.github/instructions/` and `.claude/skills/` are thin discovery layers so Copilot and Claude can auto-load the same central guidance.

| Topic | Central workflow | Claude skill |
| --- | --- | --- |
| Wiki maintenance | [ai/workflows/wiki-documentation.md](ai/workflows/wiki-documentation.md) | [.claude/skills/wiki-maintenance/](.claude/skills/wiki-maintenance/) |
| Architecture guidance | [ai/workflows/architecture.md](ai/workflows/architecture.md) | [.claude/skills/architecture-guidance/](.claude/skills/architecture-guidance/) |
| CI validation | [ai/workflows/ci-validation.md](ai/workflows/ci-validation.md) | [.claude/skills/ci-validation/](.claude/skills/ci-validation/) |
| Testing quality | [ai/workflows/testing-quality.md](ai/workflows/testing-quality.md) | [.claude/skills/testing-quality/](.claude/skills/testing-quality/) |
| Code review | [ai/workflows/code-review.md](ai/workflows/code-review.md) | [.claude/skills/code-review/](.claude/skills/code-review/) |
| Interview questions | [ai/skills/interview-questions/SKILL.md](ai/skills/interview-questions/SKILL.md) | [.claude/skills/interview-questions/](.claude/skills/interview-questions/) |
| User flow docs | [ai/workflows/user-flow-docs.md](ai/workflows/user-flow-docs.md) | [.claude/skills/user-flow-docs/](.claude/skills/user-flow-docs/) |
| Interactive HTTP API | [ai/workflows/http-testing.md](ai/workflows/http-testing.md) | [.claude/skills/http-testing/](.claude/skills/http-testing/) |

The Claude skills stay thin pointers. Edit the canonical files under `ai/workflows/` or `ai/skills/` when the behavior changes.

Claude and Copilot both rely on the repo-local wiki reminder hook wiring in [.claude/settings.json](.claude/settings.json) and [.github/hooks/wiki-reminder.json](.github/hooks/wiki-reminder.json). The shared [ai/scripts/wiki-reminder-hook.sh](ai/scripts/wiki-reminder-hook.sh) script injects lightweight wiki guidance on prompt and subagent boundaries and assumes the agent session starts from the repository root.
