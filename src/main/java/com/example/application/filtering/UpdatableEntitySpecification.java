package com.example.application.filtering;

import com.example.application.persistence.UpdatableEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable JPA Specification for filtering UpdatableEntity-based entities by date.
 * Provides type-safe, composable filtering that prevents SQL injection.
 * <p>
 * This specification filters entities by their createdAt and lastUpdatedAt fields
 * using the DateFilter object. All filters are optional and can be combined.
 * <p>
 * Example usage:
 * <pre>
 * // DateFilter is bound from the request as a @ModelAttribute parameter
 * Specification&lt;Account&gt; spec = UpdatableEntitySpecification.withDateFilter(dateFilter);
 * Page&lt;Account&gt; results = repository.findAll(spec, pageable);
 * </pre>
 * <p>
 * Combining with other specifications:
 * <pre>
 * Specification&lt;Account&gt; activeSpec = (root, query, cb) ->
 *     cb.isTrue(root.get("active"));
 * Specification&lt;Account&gt; combinedSpec = activeSpec.and(
 *     UpdatableEntitySpecification.withDateFilter(dateFilter)
 * );
 * </pre>
 */
public class UpdatableEntitySpecification {

    /**
     * Private constructor prevents instantiation.
     * This is a utility class with only static methods.
     */
    private UpdatableEntitySpecification() {
        // Prevent instantiation
    }

    /**
     * Creates a Specification that filters by createdAt and lastUpdatedAt.
     * All filters are optional - null or missing values are ignored.
     * <p>
     * Supported filters:
     * - createdAfter: createdAt >= value
     * - createdBefore: createdAt <= value
     * - updatedAfter: lastUpdatedAt >= value
     * - updatedBefore: lastUpdatedAt <= value
     *
     * @param dateFilter The date filter criteria (might be null or empty)
     * @param <T>        Entity type extending UpdatableEntity
     * @return Specification for querying (returns conjunction/true if no filters)
     */
    public static <T extends UpdatableEntity> Specification<T> withDateFilter(@Nullable DateFilter dateFilter) {
        return (Root<T> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            if (dateFilter == null || !dateFilter.hasFilters()) {
                return cb.conjunction(); // No filter = always true (select all)
            }

            List<Predicate> predicates = new ArrayList<>();

            // createdAt filters
            Instant createdAfter = dateFilter.getCreatedAfter();
            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }

            Instant createdBefore = dateFilter.getCreatedBefore();
            if (createdBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }

            // lastUpdatedAt filters
            Instant updatedAfter = dateFilter.getUpdatedAfter();
            if (updatedAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastUpdatedAt"), updatedAfter));
            }

            Instant updatedBefore = dateFilter.getUpdatedBefore();
            if (updatedBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastUpdatedAt"), updatedBefore));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Combines date filter with additional specifications using AND logic.
     * Useful for building complex queries with multiple filtering criteria.
     *
     * @param dateFilter     The date filter criteria
     * @param additionalSpec Additional filtering logic to AND with date filter
     * @param <T>            Entity type extending UpdatableEntity
     * @return Combined specification (dateFilter AND additionalSpec)
     */
    public static <T extends UpdatableEntity> Specification<T> withDateFilterAnd(
            DateFilter dateFilter,
            Specification<T> additionalSpec
    ) {
        Specification<T> dateSpec = withDateFilter(dateFilter);
        return dateSpec.and(additionalSpec);
    }
}
