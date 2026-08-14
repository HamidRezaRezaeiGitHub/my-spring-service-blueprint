package com.example.application.filtering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * Reusable audit-date range filter for {@code UpdatableEntity}-based entities that also serves as the
 * Spring MVC request parameter object.
 * <p>
 * Spring binds each flat query parameter to a nullable {@link Instant} using explicit ISO-8601
 * date-time formatting, so malformed or timezone-less values are rejected at the web boundary with a
 * structured 400 rather than being silently ignored. Bound via {@code @ModelAttribute}; flattened into
 * four independent flat query parameters in the generated OpenAPI schema via {@code @ParameterObject}.
 * Range ordering is enforced by {@link ValidDateFilterRanges}.
 * <p>
 * All bounds are optional and inclusive: {@code After} means {@code >=} and {@code Before} means
 * {@code <=}. An omitted or empty value means no filter on that bound. The same instance is passed
 * unchanged into services and JPA Specifications.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidDateFilterRanges
public class DateFilter {

    @Schema(description = "Return only records created at or after this instant (inclusive), ISO-8601 with offset or zone.",
            type = "string", format = "date-time", example = "2024-01-01T00:00:00Z")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Nullable
    private Instant createdAfter;

    @Schema(description = "Return only records created at or before this instant (inclusive), ISO-8601 with offset or zone.",
            type = "string", format = "date-time", example = "2024-12-31T23:59:59Z")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Nullable
    private Instant createdBefore;

    @Schema(description = "Return only records updated at or after this instant (inclusive), ISO-8601 with offset or zone.",
            type = "string", format = "date-time", example = "2024-11-01T00:00:00Z")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Nullable
    private Instant updatedAfter;

    @Schema(description = "Return only records updated at or before this instant (inclusive), ISO-8601 with offset or zone.",
            type = "string", format = "date-time", example = "2024-11-30T23:59:59Z")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Nullable
    private Instant updatedBefore;

    /**
     * Check if any date filters are applied.
     *
     * @return true if at least one filter is set, false otherwise
     */
    public boolean hasFilters() {
        return createdAfter != null || createdBefore != null
                || updatedAfter != null || updatedBefore != null;
    }

    /**
     * Create an empty DateFilter with no filters applied.
     *
     * @return DateFilter with all fields null
     */
    public static DateFilter empty() {
        return DateFilter.builder().build();
    }
}
