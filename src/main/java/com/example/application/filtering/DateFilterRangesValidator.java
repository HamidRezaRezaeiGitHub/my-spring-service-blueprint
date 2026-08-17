package com.example.application.filtering;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Validates that each inclusive audit-date range in {@link DateFilter} is ordered.
 * Range violations are reported against the offending {@code *Before} property so they surface as
 * field errors in the RFC 9457 validation response rather than as global object errors.
 */
public class DateFilterRangesValidator implements ConstraintValidator<ValidDateFilterRanges, DateFilter> {

    @Override
    public boolean isValid(@Nullable DateFilter value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean valid = true;
        if (isInverted(value.getCreatedAfter(), value.getCreatedBefore())) {
            addRangeViolation(context, "createdBefore", "createdAfter must not be later than createdBefore");
            valid = false;
        }
        if (isInverted(value.getUpdatedAfter(), value.getUpdatedBefore())) {
            addRangeViolation(context, "updatedBefore", "updatedAfter must not be later than updatedBefore");
            valid = false;
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
        }
        return valid;
    }

    private boolean isInverted(@Nullable Instant after, @Nullable Instant before) {
        return after != null && before != null && after.isAfter(before);
    }

    private void addRangeViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
