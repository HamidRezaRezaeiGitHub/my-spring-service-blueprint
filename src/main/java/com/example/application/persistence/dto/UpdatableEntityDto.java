package com.example.application.persistence.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UpdatableEntityDto {

    @Nullable
    @Schema(description = "Timestamp when the entity was created, in ISO-8601 UTC format", example = "2024-01-01T12:00:00Z",
            format = "date-time", nullable = true)
    private String createdAt;

    @Nullable
    @Schema(description = "Timestamp when the entity was last updated, in ISO-8601 UTC format", example = "2024-01-01T12:00:00Z",
            format = "date-time", nullable = true)
    private String lastUpdatedAt;
}
