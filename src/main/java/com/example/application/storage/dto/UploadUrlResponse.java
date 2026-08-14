package com.example.application.storage.dto;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record UploadUrlResponse(UUID fileId, URI uploadUrl, Instant expiresAt) {
}
