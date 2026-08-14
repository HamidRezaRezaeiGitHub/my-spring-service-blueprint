package com.example.application.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpMethod;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response returned by the API when a request cannot be processed")
public class ErrorResponse {

    @NonNull
    @Schema(description = "Timestamp when the error response was generated", example = "2026-01-15T10:30:00Z")
    private Instant timestamp;

    @NonNull
    @Schema(description = "HTTP status line returned for the request", example = "400 BAD_REQUEST")
    private String status;

    @NonNull
    @Schema(description = "High-level error summary", example = "Validation failed")
    private String message;

    @Builder.Default
    @Schema(description = "Optional list of specific error details", example = "[\"Name must not be blank\", \"Email must be valid\"]")
    private List<String> errors = new ArrayList<>();

    @NonNull
    @Schema(description = "Request path where the error occurred", example = "/api/v1/hello")
    private String path;

    @NonNull
    @Schema(description = "HTTP method used for the request", example = "POST")
    private HttpMethod method;

    @NonNull
    @Schema(description = "Application-specific error category", example = "VALIDATION_ERROR")
    private ResponseErrorType errorType;
}
