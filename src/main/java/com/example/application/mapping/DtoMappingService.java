package com.example.application.mapping;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DtoMappingService {

    private final Map<Class<?>, DtoMapper<?, ?>> dtoMappersByEntity = new ConcurrentHashMap<>();
    private final Map<Class<?>, DtoMapper<?, ?>> dtoMappersByDto = new ConcurrentHashMap<>();

    public DtoMappingService(List<DtoMapper<?, ?>> dtoMappersList) {
        for (DtoMapper<?, ?> dtoMapper : dtoMappersList) {
            this.dtoMappersByEntity.put(dtoMapper.getEntityClass(), dtoMapper);
            this.dtoMappersByDto.put(dtoMapper.getDtoClass(), dtoMapper);
        }
    }

    @SuppressWarnings("unchecked")
    public <R, T extends Dto<R>> @Nullable R toEntity(T dto) throws DtoMappingException {
        try {
            Class<? extends Dto<R>> dtoClass = (Class<? extends Dto<R>>) dto.getClass();
            DtoMapper<R, T> dtoMapper = (DtoMapper<R, T>) dtoMappersByDto.get(dtoClass);
            if (dtoMapper == null) {
                throw new DtoMappingException("No DTO mapper found for DTO class: " + dtoClass.getName());
            }
            return dtoMapper.toEntity(dto);
        } catch (ClassCastException e) {
            throw new DtoMappingException("Failed to cast DTO mapper for DTO class: " + dto.getClass().getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <R, T extends Dto<R>> @Nullable T toDto(R entity) throws DtoMappingException {
        try {
            Class<?> entityClass = entity.getClass();
            DtoMapper<R, T> dtoMapper = (DtoMapper<R, T>) dtoMappersByEntity.get(entityClass);
            if (dtoMapper == null) {
                throw new DtoMappingException("No DTO mapper found for Entity class: " + entityClass.getName());
            }
            return dtoMapper.toDto(entity);
        } catch (ClassCastException e) {
            throw new DtoMappingException("Failed to cast DTO mapper for Entity class: " + entity.getClass().getName(), e);
        }
    }

}
