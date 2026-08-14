package com.example.application.utility;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder().build();

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Failed to convert list to JSON string", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Failed to convert JSON string to list", e);
        }
    }
}
