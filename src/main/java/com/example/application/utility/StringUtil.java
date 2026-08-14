package com.example.application.utility;

import org.jspecify.annotations.Nullable;

public class StringUtil {

    private StringUtil() {
        // Prevent instantiation
    }

    public static String orDefault(@Nullable String str, String defaultStr) {
        return (str == null || str.isBlank()) ? defaultStr : str;
    }
}
