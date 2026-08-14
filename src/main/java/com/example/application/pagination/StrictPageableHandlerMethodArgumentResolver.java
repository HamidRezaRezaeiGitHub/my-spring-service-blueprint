package com.example.application.pagination;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Spring's standard Pageable resolver with strict public validation layered over page and size.
 * Spring otherwise normalizes malformed/out-of-range inputs to fallback values or clamps them.
 */
public final class StrictPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 100;

    public StrictPageableHandlerMethodArgumentResolver() {
        super(new SortHandlerMethodArgumentResolver());
        setFallbackPageable(PageRequest.of(0, DEFAULT_PAGE_SIZE));
        setMaxPageSize(MAX_PAGE_SIZE);
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        validatePage(webRequest.getParameterValues("page"));
        validateSize(webRequest.getParameterValues("size"));
        return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    }

    private static void validatePage(String @Nullable [] values) {
        if (values == null) {
            return;
        }
        long page = parseSingleNonBlankInteger("page", values);
        if (page < 0 || page > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("page must be between 0 and " + Integer.MAX_VALUE);
        }
    }

    private static void validateSize(String @Nullable [] values) {
        if (values == null) {
            return;
        }
        long size = parseSingleNonBlankInteger("size", values);
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }

    private static long parseSingleNonBlankInteger(String name, @Nullable String[] values) {
        if (values.length != 1 || values[0] == null || values[0].isBlank()) {
            throw new IllegalArgumentException(name + " must be a single integer value");
        }
        try {
            return Long.parseLong(values[0]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be an integer", exception);
        }
    }
}
