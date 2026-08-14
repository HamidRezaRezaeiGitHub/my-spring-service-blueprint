package com.example.application.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generated model text")
public record GenerateResponse(String content) {
}
