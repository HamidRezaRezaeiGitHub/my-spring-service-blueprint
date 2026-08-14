package com.example.application.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerateRequest(
        @NotBlank @Size(max = 4_000) String prompt
) {
}
