package com.example.authdemo.module.product.dto;

import lombok.*;
import java.util.List;

/**
 * Generic wrapper for paginated responses.
 * Provides metadata about the current page and total results.
 * @param <T> The type of the content list.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    /**
     * Utility flag to check if this is the last page.
     */
    public boolean isLast() {
        return pageNo >= totalPages - 1;
    }
}