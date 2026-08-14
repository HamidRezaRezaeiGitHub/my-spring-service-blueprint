---
name: interview-questions
description: Ask proportional decision-shaping questions before implementation or planning. Use when a user asks to be interviewed, grilled, questioned, or stress-tested on a plan; when requirement planning needs clarifying questions scaled by task complexity; when vague requirements, design choices, constraints, risks, acceptance criteria, or validation strategy need user input before proceeding.
---

# Interview Questions

Use this skill to turn uncertainty into a small set of useful decisions. Ask what cannot be answered from local context, scale depth by task size, and include a recommended answer so the user can accept a good default quickly.
Other than the unclear aspects, if you have improvement suggestions which are relevant to the requirement, include them in your questions.

## Workflow

1. Read available local context first: current prompt, active `PLAN.md` and `FINDINGS.md`, relevant wiki/docs, and narrow source areas. Do not ask a question when the repository can answer it cheaply.
2. Identify only decisions that can change the plan, implementation, validation, rollout, or risk profile.
3. Ask proportional questions:
   - `quick`: ask none unless blocked; if blocked, ask one question.
   - `standard`: ask at most 6 decision-shaping questions before proceeding.
   - `large`: walk the decision tree more deliberately; ask one question at a time when dependencies matter.
   - `risky`: ask one question at a time for unresolved safety, privacy, security, data, production, or irreversible decisions.
   - direct user invocation: continue interviewing until the user says the plan is clear enough, summarizing decisions as they settle.
4. For each question, include your recommended answer or default. If useful, include 2-3 concrete options and mark the recommendation.
5. After answers arrive, summarize settled decisions and record durable answers in `PLAN.md` or `FINDINGS.md` when a requirement workspace exists.

## Question Shape

Prefer concise multiple-choice questions with enough context to answer fast.

When asking multiple independent questions, number them.

Avoid broad interview prompts such as "What else should I know?" unless the user directly requested an open-ended interview. Prefer targeted questions about acceptance criteria, constraints, data ownership, user-facing behavior, compatibility, validation, rollout, and risk.

## Stopping Rule

Stop interviewing when the next implementation or planning step is clear enough and remaining unknowns can be recorded as assumptions. Do not let interviewing become ceremony for quick or obvious work.
