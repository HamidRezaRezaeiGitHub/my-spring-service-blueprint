package com.example.application.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CaseInsensitiveStringToEnumConverterFactoryTest {

    private enum Example {FIRST_VALUE}

    @Test
    void convert_shouldIgnoreCase_whenValueIsKnown() {
        // Arrange
        var converter = new CaseInsensitiveStringToEnumConverterFactory().getConverter(Example.class);

        // Act and assert
        assertEquals(Example.FIRST_VALUE, converter.convert("first_value"));
    }

    @Test
    void convert_shouldRejectValue_whenValueIsUnknown() {
        // Arrange
        var converter = new CaseInsensitiveStringToEnumConverterFactory().getConverter(Example.class);

        // Act and assert
        assertThrows(IllegalArgumentException.class, () -> converter.convert("unknown"));
    }
}
