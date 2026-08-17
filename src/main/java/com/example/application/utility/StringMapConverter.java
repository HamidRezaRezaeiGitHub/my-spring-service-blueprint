package com.example.application.utility;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.Map;

@Converter
public class StringMapConverter implements AttributeConverter<Map<String, String>, String> {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable Map<String, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return JSON_MAPPER.writeValueAsString(attribute);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Failed to convert map to JSON string", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return JSON_MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Failed to convert JSON string to map", e);
        }
    }
}
