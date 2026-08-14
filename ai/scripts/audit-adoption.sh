#!/bin/sh
set -eu

status=0

check_file() {
  path=$1

  if [ -e "$path" ]; then
    echo "ok      $path"
  else
    echo "missing $path"
    status=1
  fi
}

check_executable() {
  path=$1

  if [ -x "$path" ]; then
    echo "ok      $path executable"
  else
    echo "missing $path executable bit"
    status=1
  fi
}

pack_version() {
  path=$1

  if [ -f "$path" ]; then
    sed -n 's/^version:[ ]*//p' "$path" | sed -n '1p'
  fi

  return 0
}

warn_placeholders() {
  path=$1

  [ -e "$path" ] || return 0

  if [ -d "$path" ]; then
    find "$path" -type f -exec grep -nH 'TODO\|YYYY-MM-DD' {} \; 2>/dev/null \
      | while IFS= read -r match; do
          echo "warning placeholder $match"
        done
  else
    grep -nH 'TODO\|YYYY-MM-DD' "$path" 2>/dev/null \
      | while IFS= read -r match; do
          echo "warning placeholder $match"
        done
  fi
}

check_file "AGENTS.md"
check_file "CLAUDE.md"
check_file "GEMINI.md"
check_file ".claude/settings.json"
check_file ".codex/hooks.json"
check_file ".gemini/settings.json"
check_file ".github/copilot-instructions.md"
check_file ".github/hooks/wiki-reminder.json"
check_file ".github/instructions/wiki.instructions.md"
check_file ".github/instructions/architecture.instructions.md"
check_file ".github/instructions/ci-validation.instructions.md"
check_file ".github/instructions/testing-quality.instructions.md"
check_file ".github/instructions/code-review.instructions.md"
check_file ".github/instructions/interview-questions.instructions.md"
check_file ".claude/commands/start-requirement.md"
check_file ".claude/skills/handoff/SKILL.md"
check_file ".claude/skills/interview-questions/SKILL.md"
check_file ".gemini/skills/handoff/SKILL.md"
check_file ".gemini/skills/interview-questions/SKILL.md"
check_file "ai/README.md"
check_file "ai/pack.yaml"
check_file "ai/skills/handoff/SKILL.md"
check_file "ai/skills/handoff/agents/openai.yaml"
check_file "ai/skills/interview-questions/SKILL.md"
check_file "ai/skills/interview-questions/agents/openai.yaml"
check_file "ai/prompts/session/start-requirement.md"
check_file "ai/prompts/session/start-requirement-planning-only.md"
check_file "ai/prompts/session/continue-requirement.md"
check_file "ai/prompts/session/shape-requirement-from-keywords.md"
check_file "ai/prompts/session/review-requirement.md"
check_file "ai/prompts/session/analyze-requirements-stats.md"
check_file "ai/workflows/workflow-dispatch.md"
check_file "ai/workflows/requirement-planning.md"
check_file "ai/workflows/wiki-documentation.md"
check_file "ai/workflows/architecture.md"
check_file "ai/workflows/command-execution.md"
check_file "ai/workflows/systematic-debugging.md"
check_file "ai/workflows/ci-validation.md"
check_file "ai/workflows/testing-quality.md"
check_file "ai/workflows/code-review.md"
check_file "ai/workflows/vibe-coding-translation.md"
check_file "ai/templates/requirement/PLAN.md"
check_file "ai/templates/requirement/FINDINGS.md"
check_file "ai/templates/wiki/index.md"
check_file "ai/templates/wiki/log.md"
check_file "ai/templates/wiki/page.md"
check_file "ai/scripts/wiki-reminder-context.sh"
check_file "ai/scripts/wiki-reminder-hook.sh"
check_file "ai/scripts/start-requirement.sh"
check_file "ai/scripts/requirement-status.sh"
check_file "ai/scripts/list-requirements.sh"
check_file "ai/scripts/lint-requirements.sh"
check_file "ai/scripts/audit-adoption.sh"
check_file "ai/scripts/wiki-lint.sh"
check_file "wiki/index.md"
check_file "wiki/log.md"
check_file "wiki/architecture/system-overview.md"
check_file "wiki/architecture/tech-stack.md"
check_file "wiki/architecture/data-flow.md"
check_file "wiki/architecture/integration-points.md"
check_file "wiki/architecture/decisions.md"
check_file "wiki/domain/ubiquitous-language.md"
check_file "wiki/guides/testing.md"

check_executable "ai/scripts/start-requirement.sh"
check_executable "ai/scripts/wiki-reminder-context.sh"
check_executable "ai/scripts/wiki-reminder-hook.sh"
check_executable "ai/scripts/list-requirements.sh"
check_executable "ai/scripts/lint-requirements.sh"
check_executable "ai/scripts/audit-adoption.sh"
check_executable "ai/scripts/wiki-lint.sh"

warn_placeholders "AGENTS.md"
warn_placeholders "wiki"
warn_placeholders "ai/templates/requirement/PLAN.md"
warn_placeholders "ai/templates/requirement/FINDINGS.md"

installed_version=$(pack_version "ai/pack.yaml")
source_version=$(pack_version "packs/default/ai/pack.yaml")

if [ -n "$installed_version" ]; then
  echo "info    installed pack version: $installed_version"
else
  echo "info    installed pack version: unknown"
fi

if [ -n "$source_version" ]; then
  echo "info    source pack version: $source_version"

  if [ -n "$installed_version" ] && [ "$installed_version" != "$source_version" ]; then
    echo "warning pack-version installed ai/pack.yaml is $installed_version; source packs/default/ai/pack.yaml is $source_version"
  fi
fi

exit "$status"
