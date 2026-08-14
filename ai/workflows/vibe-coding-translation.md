---
name: vibe-coding-translation
use_when: Translating non-developer, stakeholder, designer, product, or vibe-style input into concrete requirements.
---

# Vibe Coding Translation Workflow

Use this workflow when a request is written by a non-developer, business stakeholder, designer, product person, or anyone describing the desired outcome in casual, UX, domain, or "vibe coding" language.

## Goal

Translate the request into an implementation-ready requirement before treating it like normal engineering work.

The translated requirement should preserve the user's intent while making the scope, expected behavior, affected surfaces, risks, and validation approach explicit.

## Detection

The request may need translation when it:

- describes user-visible behavior instead of code changes,
- uses business, product, design, or domain language instead of file/class/function names,
- says how something should "feel", "look", "work", or "flow",
- asks for a screen, page, chart, copy, data, workflow, or policy change without implementation detail,
- is vague about edge cases, failure states, permissions, or data boundaries,
- comes from a known non-technical requester.

Do not over-apply this workflow to precise developer tasks. If the request already names files, APIs, commands, data structures, and expected tests clearly, normal requirement planning is enough.

## Translation Steps

Before implementation:

1. Restate the request in concrete product/behavior terms.
2. Preserve canonical domain terms. Follow `Domain Language` below.
3. Identify affected users, screens, APIs, workflows, data, or operational surfaces.
4. Identify acceptance criteria observable by a human.
5. Identify open questions, assumptions, ambiguous terms, and synonyms.
6. Identify likely technical areas to inspect, using the wiki first when available.
7. Identify validation: tests, manual checks, CI expectations, and wiki updates.

Record the translation in `requirements/<slug>/PLAN.md`.

## Clarifying Questions

Ask a concise question before implementation when ambiguity could change:

- user-visible behavior,
- data correctness,
- permissions/security/privacy,
- business rules,
- destructive operations,
- payment, legal, or compliance behavior,
- scope or expected acceptance criteria.

If the ambiguity is low-risk, make a reasonable assumption and record it in the plan.

## Domain Language

When translating stakeholder or vibe-style language:

- Preserve the user's domain meaning even when the implementation uses different names.
- Prefer canonical terms from `wiki/domain/ubiquitous-language.md` when present.
- If the user uses a synonym, overloaded word, or vague term, record the mapping in `PLAN.md` or `FINDINGS.md`.
- If a term is likely durable across future requirements, propose adding or updating it in the wiki.
- Do not invent a canonical term when the business meaning is unclear; ask a proportional clarifying question with a recommended default.

## Acceptance Criteria

Translate vague asks into observable outcomes:

- What will the user see or be able to do?
- What should not change?
- What edge cases should be handled?
- What error or empty states matter?
- How can a reviewer verify the change?

## Communication

Use plain language when summarizing back to the requester:

- what changed from the user's perspective,
- where to see or test it,
- what remains out of scope,
- what validation was performed.

## Safety

Do not hide uncertainty behind confident implementation. Vibe-style requests often omit important constraints. Translate first, then build.
