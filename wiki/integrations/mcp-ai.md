---
title: MCP and AI
domain: architecture
tags: [mcp, ai, openai, adapters]
status: current
last_updated: 2026-08-13
---

# MCP and AI

`HelloController` and `HelloMcpTools` both delegate to `HelloService`. Activate the `mcp` profile to expose the stateless `/mcp` transport; it is disabled by default and remains authenticated because the security chain does not permit that path.

MCP clients must send both `application/json` and `text/event-stream` in `Accept`, following the Spring AI stateless WebMVC transport contract.

`AiController` and `AiMcpTools` delegate to `AiService`. With `spring.ai.model.chat=none`, generation returns a structured 503 and startup needs no API key. Activate `openai` and set `OPENAI_API_KEY` to create the `ChatClient`; the profile also sets a 30-second request timeout and bounded completion tokens. Tests mock this boundary and never call a paid service.
