#!/bin/sh
set -eu

SCRIPT_DIR=$(CDPATH= cd "$(dirname "$0")" && pwd)
PROJECT_ROOT=$(CDPATH= cd "$SCRIPT_DIR/../.." && pwd)
WIKI_INDEX="$PROJECT_ROOT/wiki/index.md"
WIKI_WORKFLOW="$PROJECT_ROOT/ai/workflows/wiki-documentation.md"
WIKI_INSTRUCTIONS="$PROJECT_ROOT/.github/instructions/wiki.instructions.md"

strip_frontmatter() {
  awk '
    NR == 1 && $0 == "---" { in_frontmatter = 1; next }
    in_frontmatter && $0 == "---" { in_frontmatter = 0; next }
    !in_frontmatter { print }
  ' "$1"
}

extract_index_overview() {
  [ -f "$WIKI_INDEX" ] || return 0

  awk '
    /^## Overview$/ { in_overview = 1; next }
    in_overview && /^## / { exit }
    in_overview { print }
  ' "$WIKI_INDEX" | sed '/^[[:space:]]*$/d' | sed -n '1,12p'
}

extract_instruction_excerpt() {
  if [ -f "$WIKI_WORKFLOW" ]; then
    strip_frontmatter "$WIKI_WORKFLOW" | sed '/^[[:space:]]*$/d' | sed -n '1,12p'
    return 0
  fi

  [ -f "$WIKI_INSTRUCTIONS" ] || return 0

  strip_frontmatter "$WIKI_INSTRUCTIONS" | sed '/^[[:space:]]*$/d' | sed -n '1,10p'
}

if [ ! -f "$WIKI_INDEX" ] && [ ! -f "$WIKI_WORKFLOW" ] && [ ! -f "$WIKI_INSTRUCTIONS" ]; then
  exit 0
fi

printf '%s\n' 'Project wiki reminder:'
printf '%s\n' '- This repository includes a committed project wiki.'

if [ -f "$WIKI_INDEX" ]; then
  printf '%s\n' '- Start with wiki/index.md before broad source search.'
fi

if [ -f "$WIKI_WORKFLOW" ]; then
  printf '%s\n' '- Follow ai/workflows/wiki-documentation.md for wiki lookup and maintenance guidance.'
elif [ -f "$WIKI_INSTRUCTIONS" ]; then
  printf '%s\n' '- Follow .github/instructions/wiki.instructions.md when reading or editing wiki content.'
fi

overview=$(extract_index_overview)
if [ -n "$overview" ]; then
  printf '\n%s\n' 'wiki/index.md overview excerpt:'
  printf '%s\n' "$overview"
fi

instructions=$(extract_instruction_excerpt)
if [ -n "$instructions" ]; then
  if [ -f "$WIKI_WORKFLOW" ]; then
    printf '\n%s\n' 'ai/workflows/wiki-documentation.md excerpt:'
  else
    printf '\n%s\n' '.github/instructions/wiki.instructions.md excerpt:'
  fi
  printf '%s\n' "$instructions"
fi