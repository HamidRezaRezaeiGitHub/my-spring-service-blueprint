package com.example.application.pagination;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.function.Function;

/**
 * Static utility class for building paginated HTTP responses.
 * <p>
 * This utility provides standardized pagination headers and response construction.
 * All methods are static - no instantiation required or possible (private constructor).
 * Encapsulates pagination metadata, Link headers (RFC 5988), and DTO mapping logic.
 * <p>
 * Usage:
 * <pre>
 * import static com.example.application.pagination.PagedResponseBuilder.build;
 *
 * // With mapper function to transform entities to DTOs
 * return build(entityPage, entity -> mapper.toDto(entity), "/api/v1/resource");
 *
 * // With already mapped page of DTOs
 * return build(dtoPage, "/api/v1/resource");
 * </pre>
 * <p>
 * Headers added:
 * - X-Total-Count: Total number of elements
 * - X-Total-Pages: Total number of pages
 * - X-Page: Current page number (0-based)
 * - X-Size: Page size
 * - Link: RFC 5988 pagination links (first, prev, next, last)
 */
public final class PagedResponseBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private PagedResponseBuilder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Build a paginated response with standard headers and mapped content.
     *
     * @param page     The Spring Data Page object containing pagination metadata
     * @param mapper   Function to map from entity to DTO (if null, returns entities directly)
     * @param basePath The base URL path for Link header generation (e.g., "/api/v1/projects")
     * @param <E>      Entity type
     * @param <T>      DTO type
     * @return ResponseEntity with mapped content and pagination headers
     */
    public static <E, T> ResponseEntity<List<T>> build(Page<E> page, @Nullable Function<E, T> mapper, String basePath) {
        // Map entities to DTOs if mapper is provided
        @SuppressWarnings("unchecked")
        List<T> content = mapper != null
                ? page.getContent().stream().map(mapper).toList()
                : (List<T>) page.getContent();

        // Create pagination headers
        HttpHeaders headers = createPaginationHeaders(page, basePath);

        return ResponseEntity.ok().headers(headers).body(content);
    }

    /**
     * Build a paginated response when content is already mapped to DTOs.
     *
     * @param page     The Spring Data Page object containing pagination metadata
     * @param basePath The base URL path for Link header generation
     * @param <T>      DTO type
     * @return ResponseEntity with content and pagination headers
     */
    public static <T> ResponseEntity<List<T>> build(Page<T> page, String basePath) {
        HttpHeaders headers = createPaginationHeaders(page, basePath);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Creates pagination headers for the response.
     * Adds X-* custom headers and RFC 5988 Link header.
     *
     * @param page     The page object containing pagination metadata
     * @param basePath The base URL path for link generation
     * @return HttpHeaders with pagination information
     */
    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    private static HttpHeaders createPaginationHeaders(Page<?> page, String basePath) {
        HttpHeaders headers = new HttpHeaders();

        // Add custom pagination headers
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.getTotalPages()));
        headers.add("X-Page", String.valueOf(page.getNumber()));
        headers.add("X-Size", String.valueOf(page.getSize()));

        // Add RFC 5988 Link header
        String linkHeader = buildLinkHeader(page, basePath);
        if (!linkHeader.isEmpty()) {
            headers.add(HttpHeaders.LINK, linkHeader);
        }

        return headers;
    }

    /**
     * Builds RFC 5988 Link header with pagination links.
     * Includes first, prev, next, and last page links.
     *
     * @param page     The page object
     * @param basePath The base URL path
     * @return Link header string
     */
    private static String buildLinkHeader(Page<?> page, String basePath) {
        StringBuilder linkHeader = new StringBuilder();

        UriComponentsBuilder baseUrl = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(basePath);

        // First page link
        linkHeader.append(formatLink(baseUrl, 0, page.getSize(), "first"));

        // Previous page link
        if (page.hasPrevious()) {
            linkHeader.append(", ").append(formatLink(baseUrl, page.getNumber() - 1, page.getSize(), "prev"));
        }

        // Next page link
        if (page.hasNext()) {
            linkHeader.append(", ").append(formatLink(baseUrl, page.getNumber() + 1, page.getSize(), "next"));
        }

        // Last page link
        if (page.getTotalPages() > 0) {
            linkHeader.append(", ").append(formatLink(baseUrl, page.getTotalPages() - 1, page.getSize(), "last"));
        }

        return linkHeader.toString();
    }

    private static String formatLink(UriComponentsBuilder baseUrl, int page, int size, String relation) {
        String href = baseUrl.cloneBuilder()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .toUriString();
        return String.format("<%s>; rel=\"%s\"", href, relation);
    }
}
