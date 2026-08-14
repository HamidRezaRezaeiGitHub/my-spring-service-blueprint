package com.example.application.filtering;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Class-level Bean Validation constraint asserting that both inclusive audit-date ranges are ordered:
 * {@code createdAfter <= createdBefore} and {@code updatedAfter <= updatedBefore}. Either bound may be
 * absent, and equal bounds are valid.
 * <p>
 * A class-level constraint is used (rather than an {@code @AssertTrue} pseudo-getter) so range validation
 * never appears as a synthetic query parameter in the generated OpenAPI schema.
 */
@Documented
@Constraint(validatedBy = DateFilterRangesValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidDateFilterRanges {

    String message() default "Date range is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
