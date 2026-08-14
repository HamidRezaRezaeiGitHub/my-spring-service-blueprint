package com.example.application.persistence;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class UpdatableEntity {

    @Nullable
    private Instant createdAt;

    @Nullable
    private Instant lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        if (createdAt == null) {
            setCreatedAt(now);
        } else {
            setCreatedAt(createdAt.truncatedTo(ChronoUnit.MICROS));
        }
        if (lastUpdatedAt == null) {
            setLastUpdatedAt(now);
        } else {
            setLastUpdatedAt(lastUpdatedAt.truncatedTo(ChronoUnit.MICROS));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        setLastUpdatedAt(Instant.now().truncatedTo(ChronoUnit.MICROS));
    }
}
