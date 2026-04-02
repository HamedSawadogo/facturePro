package org.facturepro.backoffice.shared.web;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper de pagination standard pour toutes les réponses de liste.
 */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {
    public static <T> PageResponse<T> of(final Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}