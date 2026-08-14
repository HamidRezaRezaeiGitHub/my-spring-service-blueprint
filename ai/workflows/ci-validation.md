---
name: ci-validation
use_when: Before finishing implementation work, before final two-pass code review, and before committing or opening a pull request.
---

# CI Validation Workflow

Use this workflow before finishing implementation work, before the final two-pass code review, and especially before committing or opening a pull request.

## Goal

The agent should leave the repository in a state that is expected to pass the project's configured Continuous Integration checks.

## Discover CI Expectations

Review existing CI configuration instead of inventing generic commands.

Look for:

- `.github/workflows/*.yml`
- `.github/workflows/*.yaml`
- `Jenkinsfile`
- `.gitlab-ci.yml`
- `.circleci/config.yml`
- `azure-pipelines.yml`
- build scripts or task runners referenced by those files
- project docs or wiki pages about CI, release, deployment, or validation

If a project wiki exists, start with `wiki/index.md` and any testing, CI/CD, development, or operations pages.

## Local Validation

Before finishing:

1. Identify the relevant CI checks for the files changed.
2. Run the smallest meaningful local equivalent first.
3. Run broader validation when shared code, build configuration, public API, or cross-cutting behavior changed.
4. Use `ai/workflows/command-execution.md` to keep output concise.
5. If a required check cannot be run locally, state why and what would need to run in CI.

## Baseline Versus Final Validation

For `standard`, `large`, or `risky` implementation work, the agent should also establish a baseline before source edits when a safe command can be identified.

- Baseline verification answers: "Did the relevant checks already pass before my changes?"
- Final validation answers: "Do the relevant checks pass after my changes?"
- If baseline failed before edits, keep that failure distinct from failures introduced by the requirement.
- If final validation fails in a way that matches the baseline failure, document it as pre-existing unless the current changes made it worse.
- If no baseline was run, say so in `PLAN.md` and avoid claiming that later failures are unrelated without evidence.

## Before Final Review, Commit, Or PR

- Run tests relevant to the change.
- Run formatter/linter/typecheck/build checks when the project has them.
- Check that generated files, lockfiles, and dependency changes are intentional.
- Do not modify CI files unless the user requested it or the requirement requires it.
- Do not claim CI will pass unless equivalent checks were run or the limitations are clearly stated.
- After validation, use `ai/workflows/code-review.md` for the final two-pass review. If review changes source files, rerun the smallest meaningful validation affected by those changes.

## Failure Handling

If validation fails:

- read enough output to understand the failure,
- fix failures caused by the current change,
- do not hide failures by skipping tests or weakening assertions,
- document unrelated pre-existing failures separately.
