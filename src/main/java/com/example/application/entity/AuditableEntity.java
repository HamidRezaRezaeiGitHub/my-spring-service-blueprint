package com.example.application.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Base class for entities that record creation and last-update instants.
 *
 * <p>It deliberately does not define {@code equals} or {@code hashCode}: mutable audit values must
 * never determine entity identity.</p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AuditableEntity {

    @Nullable
    @Schema(description = "Creation instant assigned when the entity is first persisted", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Nullable
    @Schema(description = "Most recent persistence update instant", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        createdAt = now;
        lastUpdatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }
}
