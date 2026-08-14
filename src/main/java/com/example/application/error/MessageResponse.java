package com.example.application.error;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Generic API response DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Generic API response containing operation status and message")
public class MessageResponse {

    @Schema(description = "Timestamp when the response was generated", example = "2026-01-15T10:30:00Z", format = "date-time")
    @NotNull(message = "Timestamp is required")
    private Instant timestamp;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "HTTP status text of the response", example = "200 OK")
    @NotBlank(message = "Status is required")
    private String status;

    @Schema(description = "Human-readable message related to the response", example = "Operation completed successfully")
    @NotBlank(message = "Message is required")
    private String message;
}
