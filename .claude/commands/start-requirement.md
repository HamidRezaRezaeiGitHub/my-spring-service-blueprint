---
allowed-tools: Bash(ai/scripts/start-requirement.sh:*), Read, Edit
description: Start or resume a local requirement workspace with PLAN.md and a requirement branch.
---

# Start Requirement

Run the requirement planning workflow for: `$ARGUMENTS`

Use `ai/scripts/start-requirement.sh "$ARGUMENTS"` when a title is provided. If no title is provided, infer a concise title from the active task, then use the script.

Before running the script, check the current branch and working tree with `git branch --show-current` and `git status --short`. If the user is on an unrelated feature/topic branch or has uncommitted changes, clarify with them whether to switch, stay (`--stay-on-current-branch`), or commit/stash first. If untracked files are present and the script would switch branches, decide whether to commit, stash, remove, ignore, or intentionally carry them before proceeding. Skip the prompt when the current branch is `main`/`master` with a clean tree. The script will warn on branch switches from non-main branches, warn when untracked files are present before a switch, and abort if the tracked working tree is dirty.

By default the script creates or switches to the expected requirement branch. If the current branch is already the right feature branch for this work, pass `--stay-on-current-branch` and record that branch choice in `PLAN.md`.

After the script runs:

- read the generated `requirements/<slug>/PLAN.md`,
- read the generated `requirements/<slug>/FINDINGS.md`,
- update TODOs with the user's requirement and first plan,
- confirm the current branch matches the plan or that `--stay-on-current-branch` was intentional,
- continue from the plan.
