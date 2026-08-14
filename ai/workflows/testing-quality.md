---
name: testing-quality
use_when: Creating, editing, reviewing, or validating tests.
---

# Testing Quality Workflow

Use this workflow when creating, editing, reviewing, or validating tests.

## Context First

Before writing tests:

1. Read `wiki/index.md` if it exists.
2. Read any wiki page about testing, quality, architecture, domain behavior, or the feature under test. In this blueprint, start with `wiki/guides/testing.md`.
3. Review nearby existing tests for local patterns and utilities.
4. Inspect source code narrowly after the wiki and existing tests establish context.

If the project has no testing wiki page and the work is test-heavy, consider creating or updating one.

For this blueprint specifically:

- Spring-context tests use a real PostgreSQL 18 Testcontainers setup; Docker is required.
- Keep test helpers local until multiple behavior-focused tests genuinely need them; the reduced template does not maintain a global fixture graph.
- New JSON controller integration coverage may use `RestTestClient`; use `MockMvc` when direct MVC, security, OpenAPI, or MCP assertions are clearer.
- Keep Arrange/Act/Assert comments inside every test, including parameterized tests.

## QA Mindset

Act like a critical QA engineer, not a test-count maximizer.

Tests should try to catch real bugs:

- important happy paths,
- edge cases and boundary values,
- invalid inputs and error paths,
- authorization or permission boundaries,
- state transitions,
- persistence or serialization behavior,
- concurrency, ordering, timing, or async behavior when relevant,
- regressions implied by the requirement.

## Avoid Low-Value Tests

Do not add tests that mainly verify:

- the programming language works,
- the framework behaves as documented,
- mocks return values they were configured to return,
- trivial getters/setters with no behavior,
- implementation details that can change without changing behavior.

## Integrity Rules

- A test exposing a real bug should fail until the bug is fixed.
- Preserve at least one focused failing regression test and record its red result before fixing the defect.
- Do not silently skip, weaken, or rewrite a failing test just to pass.
- Do not update expected values without understanding whether behavior intentionally changed.
- Prefer deterministic tests over sleeps, fixed delays, real clocks, or external services.
- Name tests for behavior and condition, not implementation mechanics.

## Test Organization

- Use `@Nested` classes when a growing class has coherent operations or scenario groups that make navigation and shared setup clearer. Do not add nesting around isolated tests merely for consistency.
- Use parameterized tests when cases share the same setup, action, and assertion contract and differ only in inputs or expected values. Keep separate tests when a case represents a distinct behavior, failure mode, or setup.
- Split a test when it verifies unrelated behavior or failure modes. Merge tests only when the combined test still communicates one behavior contract clearly.
- Remove stale, duplicated, generated, or implementation-narration comments. Keep AAA markers and concise comments that explain non-obvious setup or why regression coverage exists.
- Follow the local naming form `operation_shouldOutcome_whenCondition()`. Inside a clearly named `@Nested` operation group, `shouldOutcome_whenCondition()` is sufficient.

## Test Layer Choice

- Prefer plain unit tests for isolated business decisions, calculations, mappings, and error policy that do not require Spring.
- Use `@DataJpaTest` for PostgreSQL queries, mappings, constraints, Flyway-backed schema behavior, and service slices whose persistence boundary is material.
- Use `@SpringBootTest` for MVC serialization/binding, filters, security, application wiring, and end-to-end transactional flows.
- Improving the pyramid means moving behavior to the lowest layer that can prove it reliably. Do not remove integration coverage for a boundary that a mock or unit test cannot exercise.

## Coverage Strategy

Start from the risk created by the requirement. Cover behavior that could break, not every line mechanically.

For each meaningful change, ask:

- What should now work?
- What should still be rejected?
- What boundary might be mishandled?
- What existing behavior must not regress?
- What integration boundary is most likely to fail?

Regression-prevention tests are valuable even when the current implementation already passes them, provided they name and protect a meaningful public, domain, persistence, security, or previously broken contract. Test count alone is not coverage. Before deleting, merging, or moving a test, identify the protected contract and the surviving equivalent coverage; record the rationale when it is not obvious from the diff.

## Running Tests

Use project-specific commands discovered from docs, package scripts, or CI configuration.

When multiple agents share one checkout, do not run Maven concurrently: compilation and Surefire use the same `target/` tree and can produce stale or missing-class failures. Let one owner serialize focused and full Maven validation while other agents perform read-only review or package-local edits.

Use `ai/workflows/command-execution.md` for concise output. If tests fail and filtered output is insufficient, rerun the failing file, test name, package, or full command with enough output to debug.

Keep Mockito strict by default. Remove unused stubs exposed by refactoring instead of making the class or stub lenient unless the test genuinely needs optional shared setup.

Before finalizing test-related work, use `ai/workflows/code-review.md` to review whether tests cover meaningful behavior and whether any failing tests were weakened or skipped.
