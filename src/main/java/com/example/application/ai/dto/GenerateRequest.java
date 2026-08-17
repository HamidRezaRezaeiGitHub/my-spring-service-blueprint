package com.example.application.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Text-generation request for the configured chat model")
public record GenerateRequest(
        @Schema(description = "Prompt sent to the configured chat model", example = "Summarize this service.",
                minLength = 1, maxLength = 4_000, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 4_000) String prompt
) {
}
