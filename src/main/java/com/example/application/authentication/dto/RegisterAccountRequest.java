package com.example.application.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration details for a verified external identity")
public record RegisterAccountRequest(
        @Schema(description = "Application-facing name for the new account", example = "Example Person",
                minLength = 1, maxLength = 120, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 120) String displayName
) {
}
