#!/bin/sh
set -eu

usage() {
  cat >&2 <<'USAGE'
Usage: ai/scripts/wiki-lint.sh [--warn-placeholders|--strict-placeholders] [wiki-dir]

Checks wiki frontmatter, relative Markdown links, and index entries.

Options:
  --warn-placeholders    Report TODO and YYYY-MM-DD placeholders without failing.
  --strict-placeholders  Report TODO and YYYY-MM-DD placeholders and fail.
USAGE
}

wiki_dir=wiki
wiki_dir_set=0
placeholder_mode=off
placeholder_warnings=0
status=0

while [ "$#" -gt 0 ]; do
  case "$1" in
    --warn-placeholders)
      placeholder_mode=warn
      ;;
    --strict-placeholders)
      placeholder_mode=strict
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
      if [ "$wiki_dir_set" -eq 1 ]; then
        echo "unexpected argument: $1" >&2
        usage
        exit 2
      fi
      wiki_dir=$1
      wiki_dir_set=1
      ;;
  esac
  shift
done

if [ ! -d "$wiki_dir" ]; then
  echo "missing wiki directory: $wiki_dir" >&2
  exit 1
fi

check_frontmatter() {
  file=$1
  name=$(basename "$file")

  if [ "$name" = "index.md" ] || [ "$name" = "log.md" ]; then
    return
  fi

  if ! sed -n '1p' "$file" | grep -qx -- "---"; then
    echo "frontmatter missing opening ---: $file"
    status=1
    return
  fi

  for key in title domain tags status last_updated; do
    if ! sed -n '1,20p' "$file" | grep -q "^$key:"; then
      echo "frontmatter missing $key: $file"
      status=1
    fi
  done
}

check_links() {
  file=$1
  dir=$(dirname "$file")
  link_status=0
  links_tmp=$(mktemp)

  grep -oE '\[[^]]+\]\([^)]+\)' "$file" > "$links_tmp" 2>/dev/null || true

  while IFS= read -r link; do
    target=$(printf "%s" "$link" | sed 's/^.*](//; s/)$//')

    case "$target" in
      http://*|https://*|mailto:*|\#*|"")
        continue
        ;;
    esac

    target_no_anchor=$(printf "%s" "$target" | sed 's/#.*$//')

    if [ -z "$target_no_anchor" ]; then
      continue
    fi

    if [ ! -e "$dir/$target_no_anchor" ]; then
      echo "broken link in $file -> $target"
      link_status=1
    fi

  done < "$links_tmp"

  rm -f "$links_tmp"
  return "$link_status"
}

check_index_entry() {
  file=$1
  rel=${file#"$wiki_dir/"}
  name=$(basename "$file")

  if [ "$name" = "index.md" ] || [ "$name" = "log.md" ]; then
    return
  fi

  if [ -f "$wiki_dir/index.md" ] && ! grep -q "$rel" "$wiki_dir/index.md"; then
    echo "missing index entry: $rel"
    status=1
  fi
}

check_placeholders() {
  file=$1

  if [ "$placeholder_mode" = "off" ]; then
    return
  fi

  placeholders_tmp=$(mktemp)
  grep -nE 'TODO|YYYY-MM-DD' "$file" > "$placeholders_tmp" 2>/dev/null || true

  if [ -s "$placeholders_tmp" ]; then
    while IFS=: read -r line content; do
      case "$placeholder_mode" in
        strict)
          echo "placeholder found in $file:$line: $content"
          status=1
          ;;
        warn)
          echo "placeholder warning in $file:$line: $content"
          placeholder_warnings=1
          ;;
      esac
    done < "$placeholders_tmp"
  fi

  rm -f "$placeholders_tmp"
}

tmp_files=$(mktemp)
find "$wiki_dir" -name '*.md' -type f | sort > "$tmp_files"

while IFS= read -r file; do
  check_frontmatter "$file"
  check_index_entry "$file"
  if ! check_links "$file"; then
    status=1
  fi
  check_placeholders "$file"
done < "$tmp_files"

rm -f "$tmp_files"

if [ "$status" -eq 0 ] && [ "$placeholder_warnings" -eq 1 ]; then
  echo "wiki lint passed with placeholder warnings: $wiki_dir"
elif [ "$status" -eq 0 ]; then
  echo "wiki lint passed: $wiki_dir"
fi

exit "$status"
