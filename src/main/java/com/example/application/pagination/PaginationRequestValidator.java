package com.example.application.pagination;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Request-level pagination policies that are intentionally outside Spring's Pageable grammar.
 */
public final class PaginationRequestValidator {

    private PaginationRequestValidator() {
        // Prevent instantiation
    }

    /**
     * Rejects ambiguous requests that ask for both unpaged and explicit page boundaries.
     */
    public static void validateUnpagedCompatibility(boolean unpaged, HttpServletRequest request) {
        if (unpaged && (request.getParameterMap().containsKey("page") || request.getParameterMap().containsKey("size"))) {
            throw new IllegalArgumentException("unpaged=true cannot be combined with page or size");
        }
    }
}
