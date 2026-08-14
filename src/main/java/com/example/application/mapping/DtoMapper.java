package com.example.application.mapping;

import org.jspecify.annotations.Nullable;

public interface DtoMapper<R, T extends Dto<R>> {

    /**
     * Get the DTO class that this mapper is responsible for.
     *
     * @return the DTO class
     */
    Class<T> getDtoClass();

    /**
     * Get the Entity class that this mapper is responsible for.
     *
     * @return the Entity class
     */
    Class<R> getEntityClass();

    /**
     * Converts an entity to its corresponding DTO.
     *
     * @param entity the entity to convert
     * @return the corresponding DTO
     */
    @Nullable T toDto(R entity) throws DtoMappingException;

    /**
     * Converts a DTO to its corresponding entity.
     *
     * @param dto the DTO to convert
     * @return the corresponding entity
     */
    @Nullable R toEntity(T dto) throws DtoMappingException;
}
