package com.example.application.hello.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transport-neutral hello result")
public record HelloResponse(
        @Schema(description = "Greeting returned by the example use case",
                example = "Hello from the Spring service blueprint!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String message
) {
}
