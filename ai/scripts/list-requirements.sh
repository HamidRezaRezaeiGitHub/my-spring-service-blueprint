#!/bin/sh
set -eu

script_dir=$(CDPATH= cd "$(dirname "$0")" && pwd)
. "$script_dir/requirement-status.sh"

usage() {
  exit_code=${1:-2}

  cat >&2 <<'USAGE'
Usage: ai/scripts/list-requirements.sh [options]

List local requirement workspaces from requirements/*/PLAN.md.

Options:
  --limit N             Show at most N requirements. Default: 10.
  --sort FIELD          Sort by created, modified, updated, status, or slug. Default: modified.
  --status STATUS       Show only requirements with this exact status.
  --category CATEGORY   Show only a status category: planning, active, blocked, parked, done, cancelled, or unknown.
  --open                Show status categories other than done or cancelled.
  --stats               Show counts by status category instead of requirement rows.
  --all                 Show all matching requirements. Overrides --limit.
  --help                Show this help.

Examples:
  ai/scripts/list-requirements.sh --sort created --limit 10
  ai/scripts/list-requirements.sh --sort modified --limit 10
  ai/scripts/list-requirements.sh --sort updated --limit 10
  ai/scripts/list-requirements.sh --status active --sort modified --limit 10
  ai/scripts/list-requirements.sh --category parked --sort modified --limit 10
  ai/scripts/list-requirements.sh --open
  ai/scripts/list-requirements.sh --stats
USAGE
  exit "$exit_code"
}

limit=10
sort_field=modified
status_filter=
category_filter=
open_only=no
show_all=no
show_stats=no

while [ "$#" -gt 0 ]; do
  case "$1" in
    --limit)
      [ "$#" -ge 2 ] || usage
      limit=$2
      shift 2
      ;;
    --sort)
      [ "$#" -ge 2 ] || usage
      sort_field=$2
      shift 2
      ;;
    --status)
      [ "$#" -ge 2 ] || usage
      status_filter=$2
      shift 2
      ;;
    --category)
      [ "$#" -ge 2 ] || usage
      category_filter=$2
      shift 2
      ;;
    --open)
      open_only=yes
      shift
      ;;
    --stats)
      show_stats=yes
      shift
      ;;
    --all)
      show_all=yes
      shift
      ;;
    --help|-h)
      usage 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage
      ;;
  esac
done

case "$sort_field" in
  created|modified|updated|status|slug)
    ;;
  *)
    echo "Unsupported sort field: $sort_field" >&2
    usage
    ;;
esac

case "$limit" in
  ''|*[!0-9]*)
    echo "--limit must be a non-negative integer" >&2
    exit 2
    ;;
esac

case "$category_filter" in
  ''|planning|active|blocked|parked|done|cancelled|unknown)
    ;;
  *)
    echo "Unsupported status category: $category_filter" >&2
    usage
    ;;
esac

if [ "$sort_field" = "updated" ]; then
  sort_field=modified
fi

if [ ! -d requirements ]; then
  echo "No requirements/ directory found." >&2
  exit 0
fi

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

tmp=${TMPDIR:-/tmp}/list-requirements.$$
trap 'rm -f "$tmp"' EXIT HUP INT TERM
: > "$tmp"

found=no

for plan in requirements/*/PLAN.md; do
  [ -f "$plan" ] || continue
  found=yes

  title=$(frontmatter_value title "$plan")
  slug=$(frontmatter_value slug "$plan")
  branch=$(frontmatter_value expected_branch "$plan")
  created=$(frontmatter_value created "$plan")
  modified=$(frontmatter_value last_modified "$plan")
  status=$(frontmatter_value status "$plan")

  [ -n "$title" ] || title=$(sed -n 's/^# Requirement Plan: //p' "$plan" | sed -n '1p')
  [ -n "$slug" ] || slug=$(legacy_metadata_value "Slug" "$plan")
  [ -n "$branch" ] || branch=$(legacy_metadata_value "Expected branch" "$plan")
  [ -n "$created" ] || created=$(legacy_metadata_value "Created" "$plan")
  [ -n "$modified" ] || modified=$(legacy_metadata_value "Last modified" "$plan")
  [ -n "$status" ] || status=$(legacy_metadata_value "Status" "$plan")

  if [ -z "$slug" ]; then
    slug=${plan#requirements/}
    slug=${slug%/PLAN.md}
  fi

  if [ -z "$title" ]; then
    title=$slug
  fi

  if [ -z "$created" ]; then
    created=unknown
  fi

  if [ -z "$modified" ]; then
    modified=$created
  fi

  if [ -z "$status" ]; then
    status=unknown
  fi

  category=$(requirement_status_category "$status")

  if [ -n "$status_filter" ] && [ "$status" != "$status_filter" ]; then
    continue
  fi

  if [ -n "$category_filter" ] && [ "$category" != "$category_filter" ]; then
    continue
  fi

  if [ "$open_only" = yes ]; then
    case "$category" in
      done|cancelled)
        continue
        ;;
    esac
  fi

  case "$sort_field" in
    created)
      sort_key=$created
      ;;
    modified)
      sort_key=$modified
      ;;
    status)
      sort_key=$status
      ;;
    slug)
      sort_key=$slug
      ;;
  esac

  printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n' \
    "$sort_key" "$category" "$status" "$created" "$modified" "$slug" "$branch" "$title" >> "$tmp"
done

if [ "$found" = no ]; then
  echo "No requirement plans found under requirements/." >&2
  exit 0
fi

if [ "$sort_field" = "status" ] || [ "$sort_field" = "slug" ]; then
  sorted=$(sort "$tmp")
else
  sorted=$(sort -r "$tmp")
fi

if [ "$show_stats" = yes ]; then
  printf 'CATEGORY\tCOUNT\n'
  for category in planning active blocked parked done cancelled unknown; do
    count=$(awk -F '\t' -v category="$category" '$2 == category { count++ } END { print count + 0 }' "$tmp")
    printf '%s\t%s\n' "$category" "$count"
  done
  total=$(awk 'END { print NR + 0 }' "$tmp")
  printf 'total\t%s\n' "$total"
elif [ ! -s "$tmp" ]; then
  printf 'CATEGORY\tSTATUS\tCREATED\tMODIFIED\tSLUG\tBRANCH\tTITLE\n'
  exit 0
elif [ "$show_all" = yes ]; then
  printf 'CATEGORY\tSTATUS\tCREATED\tMODIFIED\tSLUG\tBRANCH\tTITLE\n'
  printf '%s\n' "$sorted" | cut -f2-
elif [ "$limit" = 0 ]; then
  printf 'CATEGORY\tSTATUS\tCREATED\tMODIFIED\tSLUG\tBRANCH\tTITLE\n'
  exit 0
else
  printf 'CATEGORY\tSTATUS\tCREATED\tMODIFIED\tSLUG\tBRANCH\tTITLE\n'
  printf '%s\n' "$sorted" | sed -n "1,${limit}p" | cut -f2-
fi
