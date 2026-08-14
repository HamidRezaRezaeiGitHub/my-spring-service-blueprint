#!/bin/sh
set -eu

script_dir=$(CDPATH= cd "$(dirname "$0")" && pwd)
. "$script_dir/requirement-status.sh"

usage() {
  exit_code=${1:-2}

  cat >&2 <<'USAGE'
Usage: ai/scripts/lint-requirements.sh [requirements-dir]

Lint local requirement workspaces without modifying files.

The default requirements directory is ./requirements.
Statuses are mapped into conceptual categories:
  planning, active, blocked, parked, done, cancelled, unknown

The script reports issues as:

  requirements/example/PLAN.md
    error  missing-metadata    Missing metadata field: Last modified

Exit codes:
  0  no lint issues
  1  lint issues found
  2  usage error
USAGE
  exit "$exit_code"
}

frontmatter_value() {
  key=$1
  plan=$2

  awk -v key="$key" '
    NR == 1 && $0 == "---" { in_frontmatter = 1; next }
    in_frontmatter && $0 == "---" { exit }
    in_frontmatter {
      prefix = key ":"
      if (index($0, prefix) == 1) {
        value = substr($0, length(prefix) + 1)
        sub(/^[[:space:]]*/, "", value)
        sub(/[[:space:]]*$/, "", value)
        if (value ~ /^".*"$/) {
          value = substr(value, 2, length(value) - 2)
        }
        print value
        exit
      }
    }
  ' "$plan"
}

legacy_metadata_value() {
  field=$1
  plan=$2

  sed -n "s/^- $field: \`\\{0,1\\}\\([^\`]*\\)\`\\{0,1\\}/\\1/p" "$plan" | sed -n '1p'
}

metadata_value() {
  key=$1
  legacy_field=$2
  plan=$3

  value=$(frontmatter_value "$key" "$plan")

  if [ -z "$value" ]; then
    value=$(legacy_metadata_value "$legacy_field" "$plan")
  fi

  printf '%s\n' "$value"
}

issue() {
  path=$1
  severity=$2
  code=$3
  message=$4

  if [ "$current_path" != "$path" ]; then
    printf '%s\n' "$path"
    current_path=$path
  fi

  printf '  %-5s  %-20s  %s\n' "$severity" "$code" "$message"

  if [ "$severity" = "error" ]; then
    status=1
  elif [ "$status" -eq 0 ]; then
    status=1
  fi
}

if [ "$#" -gt 1 ]; then
  usage
fi

case "${1:-}" in
  --help|-h)
    usage 0
    ;;
esac

requirements_dir=${1:-requirements}
status=0
current_path=

if [ ! -d "$requirements_dir" ]; then
  echo "No requirements directory found: $requirements_dir" >&2
  exit 0
fi

found=no

for dir in "$requirements_dir"/*; do
  [ -d "$dir" ] || continue
  found=yes

  plan=$dir/PLAN.md
  findings=$dir/FINDINGS.md

  if [ ! -f "$plan" ]; then
    issue "$dir" error missing-plan "Missing PLAN.md"
    continue
  fi

  title=$(frontmatter_value title "$plan")
  [ -n "$title" ] || title=$(sed -n 's/^# Requirement Plan: //p' "$plan" | sed -n '1p')
  slug=$(metadata_value slug "Slug" "$plan")
  branch=$(metadata_value expected_branch "Expected branch" "$plan")
  created=$(metadata_value created "Created" "$plan")
  modified=$(metadata_value last_modified "Last modified" "$plan")
  requirement_status=$(metadata_value status "Status" "$plan")
  folder_slug=${dir##*/}

  if [ -z "$title" ]; then
    issue "$plan" error missing-title "Missing title frontmatter or '# Requirement Plan: ...' heading"
  fi

  if [ -z "$slug" ]; then
    issue "$plan" error missing-metadata "Missing metadata field: Slug"
  elif [ "$slug" != "$folder_slug" ]; then
    issue "$plan" error slug-mismatch "Folder slug '$folder_slug' does not match metadata slug '$slug'"
  fi

  if [ -z "$branch" ]; then
    issue "$plan" error missing-metadata "Missing metadata field: Expected branch"
  fi

  if [ -z "$created" ]; then
    issue "$plan" error missing-metadata "Missing metadata field: Created"
  fi

  if [ -z "$modified" ]; then
    issue "$plan" error missing-metadata "Missing metadata field: Last modified"
  fi

  if [ -z "$requirement_status" ]; then
    issue "$plan" error missing-metadata "Missing metadata field: Status"
  else
    category=$(requirement_status_category "$requirement_status")
    if [ "$category" = "unknown" ]; then
      issue "$plan" error status-uncategorized "Status '$requirement_status' does not map to a known category"
    fi
  fi

  if [ ! -f "$findings" ]; then
    issue "$plan" error missing-findings "Missing FINDINGS.md"
  fi
done

if [ "$found" = no ]; then
  echo "No requirement workspaces found under: $requirements_dir" >&2
  exit 0
fi

if [ "$status" -eq 0 ]; then
  echo "Requirement lint passed."
fi

exit "$status"
