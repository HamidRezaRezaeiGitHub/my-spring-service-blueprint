package com.example.application.storage.dto;

import java.net.URI;
import java.time.Instant;

public record DownloadUrlResponse(URI downloadUrl, Instant expiresAt) {
}
