package com.example.application.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Applies Application's module-specific sort policy to Spring-resolved {@link Pageable} values.
 *
 * <p>Spring owns request parsing. This class owns the allowed fields, endpoint default ordering,
 * deterministic tie-breaker, and sorted-unpaged conversion.</p>
 */
public final class PageablePolicy {

    private final Set<String> allowedSortFields;
    private final String defaultSortField;
    private final Sort.Direction defaultSortDirection;
    private final String tieBreakerField;

    public PageablePolicy(Set<String> allowedSortFields, String defaultSortField,
                            Sort.Direction defaultSortDirection, String tieBreakerField) {
        if (!allowedSortFields.contains(defaultSortField)) {
            throw new IllegalArgumentException("Default sort field must be client-sortable: " + defaultSortField);
        }
        this.allowedSortFields = Set.copyOf(allowedSortFields);
        this.defaultSortField = defaultSortField;
        this.defaultSortDirection = defaultSortDirection;
        this.tieBreakerField = tieBreakerField;
    }

    /**
     * Applies the module sort policy while preserving Spring-resolved page and size values.
     */
    public Pageable createPageable(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), normalizeSort(pageable.getSort()));
    }

    /**
     * Converts a Spring-resolved request to an unpaged query while retaining its validated sort.
     */
    public Pageable createUnpaged(Pageable pageable) {
        return Pageable.unpaged(normalizeSort(pageable.getSort()));
    }

    private Sort normalizeSort(Sort requestedSort) {
        Sort effectiveSort;
        if (requestedSort.isSorted()) {
            validateRequestedSort(requestedSort);
            effectiveSort = requestedSort;
        } else {
            effectiveSort = Sort.by(defaultSortDirection, defaultSortField);
        }

        return appendTieBreaker(effectiveSort);
    }

    private void validateRequestedSort(Sort sort) {
        sort.forEach(order -> validateSortField(order.getProperty()));
    }

    private String validateSortField(String field) {
        String normalized = field.trim();
        if (normalized.isEmpty() || !allowedSortFields.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported sort field: " + field);
        }
        return normalized;
    }

    private Sort appendTieBreaker(Sort sort) {
        if (sort.getOrderFor(tieBreakerField) != null) {
            return sort;
        }

        List<Sort.Order> orders = new ArrayList<>();
        sort.forEach(orders::add);
        Sort.Direction tieBreakerDirection = orders.getLast().getDirection();
        orders.add(new Sort.Order(tieBreakerDirection, tieBreakerField));
        return Sort.by(orders);
    }
}
