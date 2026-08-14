---
name: command-execution
use_when: Running terminal commands, especially commands with potentially long output.
---

# Command Execution Workflow

Use this workflow whenever running terminal commands in an agent session.

## Output Discipline

Be mindful of command output length. Prefer commands that return the smallest useful signal.

Use filtering when:

- the command is known to produce verbose logs,
- only final status lines matter,
- specific errors, warnings, failed tests, or summary lines matter,
- the command is being run as a validation check rather than as an investigation.

Common patterns:

```sh
some-command 2>&1 | tail -20
some-command 2>&1 | grep -E "ERROR|FAIL|Tests run:|BUILD SUCCESS|BUILD FAILURE"
some-command 2>&1 | grep -E "error|warning|failed|passed" | tail -30
```

If filtered output shows a failure and the details are not enough, rerun a narrower command or rerun without filtering to inspect the full output.

## Debugging

Read fuller output when:

- the failure cause is unclear,
- stack traces or diagnostics are needed,
- filtering might hide the relevant context,
- a command fails in a surprising way.

Prefer narrowing by file, test name, package, or target before reading large logs.

## Reporting

When reporting command results, summarize the meaningful lines rather than pasting long logs.
