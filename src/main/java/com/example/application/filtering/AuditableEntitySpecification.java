package com.example.application.filtering;

import com.example.application.entity.AuditableEntity;
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
 * Builds reusable audit-date predicates for {@link AuditableEntity} queries.
 */
public final class AuditableEntitySpecification {

    private AuditableEntitySpecification() {
    }

    public static <T extends AuditableEntity> Specification<T> withDateFilter(@Nullable DateFilter dateFilter) {
        return (Root<T> root, CriteriaQuery<?> _, CriteriaBuilder criteriaBuilder) -> {
            if (dateFilter == null || !dateFilter.hasFilters()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            Instant createdAfter = dateFilter.getCreatedAfter();
            if (createdAfter != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }
            Instant createdBefore = dateFilter.getCreatedBefore();
            if (createdBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }
            Instant updatedAfter = dateFilter.getUpdatedAfter();
            if (updatedAfter != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("lastUpdatedAt"), updatedAfter));
            }
            Instant updatedBefore = dateFilter.getUpdatedBefore();
            if (updatedBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("lastUpdatedAt"), updatedBefore));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static <T extends AuditableEntity> Specification<T> withDateFilterAnd(
            @Nullable DateFilter dateFilter,
            Specification<T> additionalSpecification
    ) {
        return AuditableEntitySpecification.<T>withDateFilter(dateFilter).and(additionalSpecification);
    }
}
