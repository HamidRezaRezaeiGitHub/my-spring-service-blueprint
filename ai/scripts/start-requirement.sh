#!/bin/sh
set -eu

usage() {
  cat >&2 <<'USAGE'
Usage: ai/scripts/start-requirement.sh [--stay-on-current-branch|--no-switch] "Requirement Title"

Options:
  --stay-on-current-branch  Create or resume the requirement workspace without switching branches.
  --no-switch               Alias for --stay-on-current-branch.
USAGE
}

escape_sed_replacement() {
  printf "%s" "$1" | sed 's/[\/&|\\]/\\&/g'
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

expected_branch_from_plan() {
  plan=$1

  expected_branch=$(frontmatter_value expected_branch "$plan")

  if [ -z "$expected_branch" ]; then
    expected_branch=$(sed -n 's/^- Expected branch: `\(.*\)`/\1/p' "$plan" | sed -n '1p')
  fi

  printf '%s\n' "$expected_branch"
}

ensure_requirements_local_ignore() {
  if git check-ignore -q "requirements/" 2>/dev/null; then
    return 0
  fi

  exclude_file=".git/info/exclude"
  mkdir -p ".git/info"

  if [ -f "$exclude_file" ] && grep -qx "requirements/" "$exclude_file"; then
    return 0
  fi

  if [ -s "$exclude_file" ]; then
    printf '\n' >> "$exclude_file"
  fi

  {
    printf '# Local agent requirement workspaces\n'
    printf 'requirements/\n'
  } >> "$exclude_file"

  echo "added requirements/ to $exclude_file"
}

stay_on_current_branch=0
title=

while [ "$#" -gt 0 ]; do
  case "$1" in
    --stay-on-current-branch|--no-switch)
      stay_on_current_branch=1
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    --*)
      echo "unknown option: $1" >&2
      usage
      exit 2
      ;;
    *)
      if [ -n "$title" ]; then
        echo "unexpected argument: $1" >&2
        usage
        exit 2
      fi
      title=$1
      ;;
  esac
  shift
done

if [ -z "$title" ]; then
  usage
  exit 2
fi

slug=$(printf "%s" "$title" \
  | tr '[:upper:]' '[:lower:]' \
  | sed 's/[^a-z0-9][^a-z0-9]*/-/g' \
  | sed 's/^-//' \
  | sed 's/-$//')

if [ -z "$slug" ]; then
  echo "Could not derive a requirement slug from title: $title" >&2
  exit 1
fi

if [ ! -d .git ]; then
  echo "This script must be run from a git repository root." >&2
  exit 1
fi

ensure_requirements_local_ignore

workspace="requirements/$slug"
plan="$workspace/PLAN.md"
findings="$workspace/FINDINGS.md"
branch="feature/$slug"
fallback_branch=$slug
current_branch=$(git branch --show-current)
expected_branch=

mkdir -p "$workspace"

if [ -f "$plan" ]; then
  expected_branch=$(expected_branch_from_plan "$plan")

  if [ -n "$expected_branch" ]; then
    branch=$expected_branch
  fi
fi

dirty=0
if ! git diff --quiet 2>/dev/null; then
  dirty=1
elif ! git diff --cached --quiet 2>/dev/null; then
  dirty=1
fi
untracked_files=$(git ls-files --others --exclude-standard 2>/dev/null || true)

would_switch=0
if [ "$stay_on_current_branch" -ne 1 ]; then
  if [ "$current_branch" != "$branch" ] \
     && ! { [ "$current_branch" = "$fallback_branch" ] && [ "$branch" = "feature/$slug" ]; }; then
    would_switch=1
  fi
fi

if [ "$would_switch" -eq 1 ] && [ -n "$untracked_files" ]; then
  untracked_count=$(printf '%s\n' "$untracked_files" | sed '/^$/d' | wc -l | tr -d ' ')

  echo "warning: untracked files exist before switching branches ($untracked_count)." >&2
  printf '%s\n' "$untracked_files" | sed -n '1,20s/^/  - /p' >&2
  if [ "$untracked_count" -gt 20 ] 2>/dev/null; then
    echo "  ..." >&2
  fi
  echo "warning: decide whether these files should be committed, stashed, removed, or intentionally carried." >&2
fi

if [ "$would_switch" -eq 1 ] && [ "$dirty" -eq 1 ]; then
  echo "Refusing to switch branches: working tree has uncommitted changes." >&2
  echo "Current branch: $current_branch" >&2
  echo "Target branch:  $branch" >&2
  echo "" >&2
  echo "Resolve before re-running:" >&2
  echo "  - commit or stash the changes, or" >&2
  echo "  - rerun with --stay-on-current-branch to keep $current_branch." >&2
  exit 1
fi

if [ "$would_switch" -eq 1 ] \
   && [ -n "$current_branch" ] \
   && [ "$current_branch" != "main" ] \
   && [ "$current_branch" != "master" ]; then
  echo "warning: switching from $current_branch to $branch." >&2
  echo "warning: pass --stay-on-current-branch if this requirement should continue on $current_branch." >&2
fi

if [ "$stay_on_current_branch" -eq 1 ]; then
  if [ -z "$current_branch" ]; then
    echo "Cannot stay on the current branch because git is in a detached HEAD state." >&2
    exit 1
  fi
  if [ -n "$expected_branch" ] && [ "$expected_branch" != "$current_branch" ]; then
    echo "Cannot stay on $current_branch because $plan expects branch $expected_branch." >&2
    echo "Switch to $expected_branch, choose the matching requirement title, or update the plan metadata intentionally." >&2
    exit 1
  fi
  branch=$current_branch
  if [ "$current_branch" = "main" ] || [ "$current_branch" = "master" ]; then
    echo "warning: staying on $current_branch; this option is intended for an existing feature branch" >&2
  fi
elif [ "$current_branch" = "$branch" ]; then
  :
elif [ "$current_branch" = "$fallback_branch" ] && [ "$branch" = "feature/$slug" ]; then
  branch=$current_branch
elif [ "$current_branch" = "main" ] || [ "$current_branch" = "master" ]; then
  if git show-ref --verify --quiet "refs/heads/$branch"; then
    git switch "$branch"
  else
    if git switch -c "$branch" 2>/dev/null; then
      :
    else
      echo "could not create $branch; trying $fallback_branch" >&2
      if git show-ref --verify --quiet "refs/heads/$fallback_branch"; then
        git switch "$fallback_branch"
      else
        git switch -c "$fallback_branch"
      fi
      branch=$fallback_branch
    fi
  fi
else
  if git show-ref --verify --quiet "refs/heads/$branch"; then
    git switch "$branch"
  else
    if git switch -c "$branch" 2>/dev/null; then
      :
    else
      echo "could not create $branch; trying $fallback_branch" >&2
      if git show-ref --verify --quiet "refs/heads/$fallback_branch"; then
        git switch "$fallback_branch"
      else
        git switch -c "$fallback_branch"
      fi
      branch=$fallback_branch
    fi
  fi
fi

if [ ! -f "$plan" ]; then
  created_at=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
  template="ai/templates/requirement/PLAN.md"

  if [ ! -f "$template" ] && [ -f "packs/default/ai/templates/requirement/PLAN.md" ]; then
    template="packs/default/ai/templates/requirement/PLAN.md"
  fi

  if [ ! -f "$template" ]; then
    echo "Missing template: $template" >&2
    exit 1
  fi

  escaped_title=$(escape_sed_replacement "$title")
  escaped_slug=$(escape_sed_replacement "$slug")
  escaped_branch=$(escape_sed_replacement "$branch")
  escaped_created_at=$(escape_sed_replacement "$created_at")

  sed \
    -e "s|{{TITLE}}|$escaped_title|g" \
    -e "s|{{SLUG}}|$escaped_slug|g" \
    -e "s|{{BRANCH}}|$escaped_branch|g" \
    -e "s|{{CREATED_AT}}|$escaped_created_at|g" \
    "$template" > "$plan"
  echo "created $plan"
else
  echo "found existing $plan"
fi

if [ ! -f "$findings" ]; then
  template="ai/templates/requirement/FINDINGS.md"

  if [ ! -f "$template" ] && [ -f "packs/default/ai/templates/requirement/FINDINGS.md" ]; then
    template="packs/default/ai/templates/requirement/FINDINGS.md"
  fi

  if [ ! -f "$template" ]; then
    echo "Missing template: $template" >&2
    exit 1
  fi

  escaped_title=$(escape_sed_replacement "$title")

  sed \
    -e "s|{{TITLE}}|$escaped_title|g" \
    "$template" > "$findings"
  echo "created $findings"
else
  echo "found existing $findings"
fi

echo "Requirement workspace: $workspace"
echo "Plan: $plan"
echo "Findings: $findings"
echo "Branch: $branch"
