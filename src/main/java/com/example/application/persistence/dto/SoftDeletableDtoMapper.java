package com.example.application.persistence.dto;

import com.example.application.persistence.SoftDeletable;
import org.jspecify.annotations.Nullable;

public class SoftDeletableDtoMapper extends UpdatableEntityDtoMapper {

    protected static SoftDeletableDto toDto(@Nullable SoftDeletable entity, SoftDeletableDto dto) {
        if (entity == null) {
            return dto;
        }
        dto = (SoftDeletableDto) UpdatableEntityDtoMapper.toDto(entity, dto);
        dto.setIncluded(entity.isIncluded());
        return dto;
    }

    protected static SoftDeletable toEntity(@Nullable SoftDeletableDto dto, SoftDeletable entity) {
        if (dto == null) {
            return entity;
        }
        entity = (SoftDeletable) UpdatableEntityDtoMapper.toEntity(dto, entity);
        entity.setIncluded(dto.isIncluded());
        return entity;
    }
}
