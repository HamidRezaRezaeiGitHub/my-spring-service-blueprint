package com.example.application.pagination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageablePolicyTest {
    private PageablePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new PageablePolicy(Set.of("createdAt", "displayName"),
                "createdAt", Sort.Direction.DESC, "id");
    }

    @Test
    void createPageable_shouldApplyDefaultAndTieBreaker_whenSortIsAbsent() {
        // Arrange
        var request = PageRequest.of(2, 25);

        // Act
        var result = policy.createPageable(request);

        // Assert
        assertEquals(2, result.getPageNumber());
        assertEquals(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")), result.getSort());
    }

    @Test
    void createPageable_shouldRejectSort_whenFieldIsNotAllowed() {
        // Arrange
        var request = PageRequest.of(0, 20, Sort.by("secret"));

        // Act and assert
        assertThrows(IllegalArgumentException.class, () -> policy.createPageable(request));
    }

    @Test
    void createUnpaged_shouldRetainSort_whenRequestIsUnpagedForQuery() {
        // Arrange
        var request = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "displayName"));

        // Act
        var result = policy.createUnpaged(request);

        // Assert
        assertTrue(result.isUnpaged());
        assertEquals(Sort.by(Sort.Order.asc("displayName"), Sort.Order.asc("id")), result.getSort());
    }
}
