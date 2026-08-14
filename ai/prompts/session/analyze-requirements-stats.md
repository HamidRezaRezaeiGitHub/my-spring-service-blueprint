Analyze requirement statistics.

Scope:
<paste "all local requirements", a status/category, a date range, a branch, a folder path, or "default" here>

Questions to answer:
<paste specific questions, or "summarize status distribution, stale work, blockers, and completion trends">

Follow project's installed AI instructions. Start with `ai/workflows/requirement-planning.md` as context and inspect local requirement workspaces only as much as needed. Do not create a new requirement workspace. There are scripts which would help the requirement analysis.

Summarize counts by status/category, notable stale or blocked requirements, recently active work, and any validation or handoff gaps visible from the requirement files. Avoid broad source discovery unless a requirement file points to a specific area that must be checked.

Do not modify requirement files unless I explicitly ask.
If the analysis depends on an ambiguous interpretation, use the `interview-questions` skill.

Once the analysis is complete, suggest next requirements. It could be one of the existing parked/incomplete requirements or a new requirement. Include a short rationale for each suggestion.
