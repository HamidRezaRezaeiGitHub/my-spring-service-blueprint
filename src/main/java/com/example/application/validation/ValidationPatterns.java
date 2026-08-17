package com.example.application.validation;

/**
 * Centralised regex pattern constants shared across request, response, and DTO classes.
 * <p>
 * Use as a static import so annotation attributes remain concise:
 * <pre>{@code
 * import static com.example.application.validation.ValidationPatterns.*;
 *
 * @Pattern(regexp = NON_BLANK_IF_PRESENT_PATTERN, message = "...")
 * }</pre>
 */
public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    /**
     * Accepts any string that contains at least one non-whitespace character.
     * Use on optional fields where an empty or whitespace-only value is disallowed,
     * but {@code null} (absent) is still allowed.
     */
    public static final String NON_BLANK_IF_PRESENT_PATTERN = ".*\\S.*";

    /**
     * Accepts international-style phone numbers: optional leading {@code +}, then digits,
     * spaces, parentheses, dots, and hyphens, totalling 7–30 characters, with at least one digit.
     * Example: {@code +1 (555) 123-4567}.
     */
    public static final String PHONE_PATTERN = "^(?=.*\\d)\\+?[0-9\\s().-]{7,30}$";

    /**
     * Accepts exactly three uppercase ASCII letters, matching the ISO 4217 currency code format.
     * Example: {@code USD}, {@code EUR}.
     */
    public static final String ISO_4217_CURRENCY_PATTERN = "^[A-Z]{3}$";

    /**
     * Accepts a CSS-style six-digit hex color with a leading {@code #}.
     * Both upper- and lower-case hex digits are allowed.
     * Example: {@code #1A2B3C}, {@code #ffffff}.
     */
    public static final String HEX_COLOR_PATTERN = "^#[0-9A-Fa-f]{6}$";
}
