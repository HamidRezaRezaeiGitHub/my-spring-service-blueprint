package com.example.application.storage.dto;

import com.example.application.storage.StoragePurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateUploadRequest(
        @NotNull StoragePurpose purpose,
        @NotBlank @Size(max = 255) String filename,
        @NotBlank @Size(max = 128) String contentType,
        @Positive long contentLength
) {
}
