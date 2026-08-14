package com.example.application.persistence.dto;

import com.example.application.persistence.UpdatableEntity;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public class UpdatableEntityDtoMapper {

    public static @Nullable String toString(@Nullable Instant instant) {
        return instant == null ? null : instant.toString();
    }

    public static @Nullable Instant fromString(@Nullable String instant) {
        return instant == null ? null : Instant.parse(instant);
    }

    protected static UpdatableEntityDto toDto(@Nullable UpdatableEntity entity, UpdatableEntityDto dto) {
        if (entity == null) {
            return dto;
        }
        dto.setCreatedAt(toString(entity.getCreatedAt()));
        dto.setLastUpdatedAt(toString(entity.getLastUpdatedAt()));
        return dto;
    }

    protected static UpdatableEntity toEntity(@Nullable UpdatableEntityDto dto, UpdatableEntity entity) {
        if (dto == null) {
            return entity;
        }
        entity.setCreatedAt(fromString(dto.getCreatedAt()));
        entity.setLastUpdatedAt(fromString(dto.getLastUpdatedAt()));
        return entity;
    }

}
