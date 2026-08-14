package com.example.application.hello.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transport-neutral hello result")
public record HelloResponse(String message) {
}
