package com.example.application.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAccountRequest(
        @NotBlank @Size(max = 120) String displayName
) {
}
