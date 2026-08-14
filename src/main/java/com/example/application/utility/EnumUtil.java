package com.example.application.utility;

import org.jspecify.annotations.Nullable;

import java.util.*;

public class EnumUtil {

    private EnumUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts a string to the specified enum type, case-insensitive.
     * Returns null if the value is null or cannot be converted.
     */
    public static <T extends Enum<T>> @Nullable T fromString(Class<T> enumType, @Nullable String value) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * Converts a string to the specified enum type, case-insensitive, returns defaultValue if conversion fails.
     */
    public static <T extends Enum<T>> T fromStringOrDefault(Class<T> enumType, String value, T defaultValue) {
        T result = fromString(enumType, value);
        return result != null ? result : defaultValue;
    }

    /**
     * Converts a list of strings to a list of the specified enum type, case-insensitive.
     * Ignores values that cannot be converted.
     */
    public static <T extends Enum<T>> List<T> fromStrings(@Nullable Class<T> enumType, @Nullable List<String> values) {
        if (values == null || enumType == null) return Collections.emptyList();
        List<T> result = new ArrayList<>();
        for (String value : values) {
            T enumValue = fromString(enumType, value);
            if (enumValue != null) {
                result.add(enumValue);
            }
        }
        return result;
    }

    /**
     * Converts a list of strings to a list of the specified enum type, case-insensitive,
     * returns defaultValue if conversion fails.
     */
    public static <T extends Enum<T>> List<T> fromStringsOrDefault(@Nullable Class<T> enumType, @Nullable List<String> values, T defaultValue) {
        if (values == null || enumType == null) return Collections.emptyList();
        List<T> result = new ArrayList<>();
        for (String value : values) {
            T enumValue = fromStringOrDefault(enumType, value, defaultValue);
            result.add(enumValue);
        }
        return result;
    }

    /**
     * Converts a collection of strings to a set of the specified enum type, case-insensitive.
     * Ignores values that cannot be converted. Resulting set contains unique enum values.
     */
    public static <T extends Enum<T>> Set<T> fromUniqStrings(Class<T> enumType, @Nullable Collection<String> values) {
        Set<T> result = new HashSet<>();
        if (values == null) return result;
        for (String value : values) {
            T enumValue = fromString(enumType, value);
            if (enumValue != null) {
                result.add(enumValue);
            }
        }
        return result;
    }

    /**
     * Converts a collection of strings to a set of the specified enum type, case-insensitive,
     * using defaultValue if conversion fails. Resulting set contains unique enum values.
     */
    public static <T extends Enum<T>> Set<T> fromUniqStringsOrDefault(@Nullable Class<T> enumType, @Nullable Collection<String> values, T defaultValue) {
        if (values == null || enumType == null) return Collections.emptySet();
        Set<T> result = new HashSet<>();
        for (String value : values) {
            T enumValue = fromStringOrDefault(enumType, value, defaultValue);
            result.add(enumValue);
        }
        return result;
    }

}
