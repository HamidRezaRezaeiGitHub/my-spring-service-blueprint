package com.example.application.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Pending file identifier and temporary provider upload URL")
public record UploadUrlResponse(
        @Schema(description = "Identifier used to complete and later download the upload",
                example = "7f4ec68c-05b7-4a4c-a52c-7cfb30c73de7",
                requiredMode = Schema.RequiredMode.REQUIRED) UUID fileId,
        @Schema(description = "Provider-signed upload URL", format = "uri",
                example = "https://storage.example.test/upload/signed-object",
                requiredMode = Schema.RequiredMode.REQUIRED) URI uploadUrl,
        @Schema(description = "Instant after which the signed URL is no longer valid", format = "date-time",
                example = "2026-08-17T15:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED) Instant expiresAt
) {
}
