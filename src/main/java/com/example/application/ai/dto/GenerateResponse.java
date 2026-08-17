package com.example.application.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generated model text")
public record GenerateResponse(
        @Schema(description = "Non-blank text returned by the configured chat model",
                example = "This service provides reusable REST, MCP, security, and persistence foundations.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String content
) {
}
