package com.example.application.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers shared web-boundary type conversions.
 *
 * <p>The case-insensitive String-to-enum converter lets versioned controllers accept domain enum request
 * arguments (path variables, query parameters, and their collections) while preserving the existing
 * case-insensitive public contract. Invalid values are rejected during binding and surface through the
 * standard structured 400 response.
 */
@Configuration
public class WebConversionConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CaseInsensitiveStringToEnumConverterFactory());
    }
}
