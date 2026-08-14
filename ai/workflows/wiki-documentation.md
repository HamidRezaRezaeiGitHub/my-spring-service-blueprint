---
name: wiki-documentation
use_when: Reading, maintaining, or promoting durable project knowledge into the project wiki.
---

# Wiki Documentation Workflow

Use this workflow to maintain a project-local LLM wiki: a concise, linked Markdown knowledge base that helps humans and AI agents understand the project without repeatedly scanning all source code.

## Core Idea

The wiki is a compiled knowledge layer. Source code, READMEs, ADRs, issues, requirement plans, and requirement findings remain source material. The wiki summarizes stable project knowledge: architecture, domain concepts, conventions, operations, data flows, and important decisions.

Requirement-specific findings belong in `requirements/<slug>/FINDINGS.md` first. Promote them into the wiki only when they become durable project knowledge.

Agents should use the wiki to reduce broad source scans. Follow `Lookup` below for the reading procedure.

## Continuous Maintenance

Maintain the wiki as part of normal requirement work, not as a separate documentation chore. When you discover durable project knowledge while planning, implementing, validating, or reviewing, you should either update the relevant wiki page immediately or record a specific wiki update in `PLAN.md` before finishing.

Use this promotion path:

1. Keep requirement-local discoveries in `requirements/<slug>/FINDINGS.md` while they are still tentative, narrow, or tied to the current task.
2. Promote knowledge into `wiki/` once it is stable, reusable beyond the requirement, or needed to help future agents avoid rediscovery.
3. Update `wiki/index.md` and `wiki/log.md` whenever topic pages are created, renamed, or meaningfully changed.

Examples of durable discoveries to promote:

- a newly understood architecture boundary,
- a data flow or integration behavior future changes must respect,
- a domain term, synonym, or ambiguity that will recur,
- a validated setup, test, lint, build, or CI command,
- a repeated failure mode, operational caveat, or security/privacy constraint,
- a project convention that is not obvious from file names alone.

Do not promote noisy task notes, raw command logs, speculative conclusions, or one-off implementation details. Keep those in `FINDINGS.md` or `PLAN.md`.

## Directory Structure

Recommended baseline:

```text
wiki/
  index.md
  log.md
  architecture/
  domain/
    ubiquitous-language.md
  operations/
  guides/
```

Projects may add domain-specific folders such as `ui/`, `data-models/`, `providers/`, `api/`, `infra/`, or `security/`.

## Page Types

- `wiki/index.md`: navigation map with one-line summaries for every page.
- `wiki/log.md`: chronological log of wiki updates, ingest, lint, and meaningful project changes.
- Topic pages: focused pages for architecture, domain concepts, workflows, operations, and conventions.
- Ubiquitous language page: canonical domain terms, one-sentence definitions, aliases to avoid, key relationships, and flagged ambiguities.
- Architecture pages: current system overview, tech stack, data flow, integration points, and decisions.

## Frontmatter

Every topic page should include:

```yaml
---
title: Human-readable page title
domain: architecture|domain|operations|guides|other
tags: [short, useful, tags]
status: current|draft|stale
last_updated: YYYY-MM-DD
---
```

`wiki/index.md` and `wiki/log.md` may use lighter frontmatter.

Update `last_updated` whenever page content changes.

## Lookup

At the start of non-trivial work:

1. Read `wiki/index.md` if it exists.
2. Read only the wiki pages relevant to the task.
3. Check `wiki/log.md` when recent context or prior wiki changes matter.
4. Search source code only after wiki lookup has narrowed the area.

If no wiki exists, create the baseline when the task is documentation-related or when the user asks for project orientation/documentation.

## Update Procedure

1. Update affected topic pages.
2. Update frontmatter dates and status.
3. Add or fix inline cross-links.
4. Update `wiki/index.md` summaries and links.
5. Append an entry to `wiki/log.md`.

For architecture-impacting changes, follow `ai/workflows/architecture.md` and update relevant pages under `wiki/architecture/`.

For domain-language changes, update `wiki/domain/ubiquitous-language.md` when a term becomes durable across requirements. Keep requirement-local vocabulary notes in `requirements/<slug>/FINDINGS.md` until they are stable enough for the wiki.

## Ubiquitous Language

Use the ubiquitous language page to prevent drift between stakeholder wording, requirements, tests, wiki pages, and code names.

When updating it:

- Include only domain terms a domain expert would recognize.
- Skip generic programming terms unless the project uses them as domain language.
- Use one-sentence definitions that say what the term is, not what it does.
- Pick one canonical term when synonyms compete, and list aliases to avoid.
- Flag ambiguous or overloaded terms with a recommendation.
- Capture important relationships between terms, including lifecycle, ownership, or cardinality when useful.
- Group terms by natural subdomain only when it improves scanning.

Do not rename code or rewrite broad docs just to match a new term unless the requirement asks for that migration. Record terminology mismatches first, then plan migration deliberately.

## Log Format

Use one entry per meaningful wiki event:

```markdown
## [YYYY-MM-DD] action | Subject

Brief description of what changed and why.
```

Allowed actions: `create`, `update`, `ingest`, `lint`, `query`.

## Linking

- Use relative Markdown links.
- Link inline where the related concept is discussed.
- Avoid footer-only `See also`, `References`, or `Sources` sections for internal wiki navigation.
- Prefer stable page-level links over fragile heading anchors.

## Style

- Write in present tense.
- Keep pages concise and high-level.
- Explain behavior, relationships, and decisions.
- Avoid source code except for short illustrative snippets when they clarify a concept.
- Target both humans and AI agents.
- Split pages when a reader must scroll past unrelated concepts to find what they need.

## Wiki Lint

Use the bundled script:

```sh
ai/scripts/wiki-lint.sh
```

The script checks for missing topic-page frontmatter, broken relative Markdown links, and topic pages missing from `wiki/index.md`.

Use `ai/scripts/wiki-lint.sh --warn-placeholders` to surface leftover `TODO` and `YYYY-MM-DD` wiki placeholders without failing, or `--strict-placeholders` when unfinished placeholders should fail validation. Keep the default lint compatible with starter wiki templates, which intentionally include placeholders.
