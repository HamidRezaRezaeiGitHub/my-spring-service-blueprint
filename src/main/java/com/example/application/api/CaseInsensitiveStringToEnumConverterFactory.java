package com.example.application.api;

import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Arrays;

/**
 * Case-insensitive {@link String} to {@link Enum} conversion for web request binding.
 *
 * <p>Registered ahead of Spring's built-in case-sensitive {@code StringToEnumConverterFactory} so existing
 * lowercase and mixed-case clients keep working once controllers accept domain enum arguments directly. It
 * mirrors the framework's empty-source semantics&mdash;an empty string binds to {@code null} so an optional
 * filter simply stays unset&mdash;but rejects unknown names with an {@link IllegalArgumentException} so invalid
 * input fails at the binding boundary instead of being silently dropped.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CaseInsensitiveStringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        Class<?> enumType = targetType;
        while (enumType != null && !enumType.isEnum()) {
            enumType = enumType.getSuperclass();
        }
        if (enumType == null) {
            throw new IllegalArgumentException("The target type " + targetType.getName() + " does not refer to an enum");
        }
        return new StringToEnum<>(enumType);
    }

    private record StringToEnum<T extends Enum>(Class<?> enumType) implements Converter<String, T> {

        @Override
        public @Nullable T convert(String source) {
            if (source.isEmpty()) {
                // Preserve Spring's semantics: an empty value clears an optional enum argument.
                return null;
            }
            String candidate = source.trim();
            for (Object constant : enumType.getEnumConstants()) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(candidate)) {
                    return (T) constant;
                }
            }
            throw new IllegalArgumentException("'" + source + "' is not a valid " + enumType.getSimpleName() + "; " +
                    "accepted values are " + Arrays.toString(enumType.getEnumConstants()));
        }
    }
}
