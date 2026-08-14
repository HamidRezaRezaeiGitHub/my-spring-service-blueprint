package com.example.application.filtering;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateFilterTest {

    @Test
    void hasFilters_shouldReturnFalse_whenAllBoundsAreAbsent() {
        // Arrange and act
        boolean result = DateFilter.empty().hasFilters();

        // Assert
        assertFalse(result);
    }

    @Test
    void hasFilters_shouldReturnTrue_whenOneBoundExists() {
        // Arrange
        var filter = DateFilter.builder().createdAfter(Instant.parse("2026-01-01T00:00:00Z")).build();

        // Act and assert
        assertTrue(filter.hasFilters());
    }

    @Test
    void validation_shouldRejectRange_whenBeforePrecedesAfter() {
        // Arrange
        var filter = DateFilter.builder()
                .createdAfter(Instant.parse("2026-02-01T00:00:00Z"))
                .createdBefore(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        // Act
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator().validate(filter);

            // Assert
            assertEquals(1, violations.size());
        }
    }
}
