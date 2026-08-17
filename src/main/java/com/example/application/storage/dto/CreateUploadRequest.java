package com.example.application.storage.dto;

import com.example.application.storage.StoragePurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Metadata needed to create a provider-backed upload URL")
public record CreateUploadRequest(
        @Schema(description = "Application purpose used to namespace and govern the object", example = "ATTACHMENT",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull StoragePurpose purpose,
        @Schema(description = "Original client filename", example = "invoice.pdf", minLength = 1,
                maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255) String filename,
        @Schema(description = "Media type the client will upload", example = "application/pdf", minLength = 1,
                maxLength = 128, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 128) String contentType,
        @Schema(description = "Expected object size in bytes", example = "245760", minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive long contentLength
) {
}
