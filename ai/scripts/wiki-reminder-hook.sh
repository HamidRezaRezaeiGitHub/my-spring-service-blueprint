#!/bin/sh
set -eu

event_name=${1:-UserPromptSubmit}
output_mode=${2:-native}

SCRIPT_DIR=$(CDPATH= cd "$(dirname "$0")" && pwd)
PROJECT_ROOT=$(CDPATH= cd "$SCRIPT_DIR/../.." && pwd)
HELPER="$PROJECT_ROOT/ai/scripts/wiki-reminder-context.sh"

if [ ! -x "$HELPER" ]; then
  exit 0
fi

context=$("$HELPER")
if [ -z "$context" ]; then
  exit 0
fi

escaped_context=$(printf '%s' "$context" | awk '
  BEGIN { first = 1 }
  {
    gsub(/\\/, "\\\\")
    gsub(/"/, "\\\"")
    if (!first) {
      printf "\\n"
    }
    printf "%s", $0
    first = 0
  }
')

case "$output_mode" in
  copilot)
    printf '{"additionalContext":"%s"}\n' "$escaped_context"
    ;;
  *)
    printf '{"hookSpecificOutput":{"hookEventName":"%s","additionalContext":"%s"}}\n' "$event_name" "$escaped_context"
    ;;
esac