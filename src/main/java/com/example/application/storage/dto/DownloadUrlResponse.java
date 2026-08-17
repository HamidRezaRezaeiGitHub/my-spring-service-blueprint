package com.example.application.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;

@Schema(description = "Temporary URL for downloading a verified stored object")
public record DownloadUrlResponse(
        @Schema(description = "Provider-signed download URL", format = "uri",
                example = "https://storage.example.test/download/signed-object",
                requiredMode = Schema.RequiredMode.REQUIRED) URI downloadUrl,
        @Schema(description = "Instant after which the signed URL is no longer valid", format = "date-time",
                example = "2026-08-17T15:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED) Instant expiresAt
) {
}
