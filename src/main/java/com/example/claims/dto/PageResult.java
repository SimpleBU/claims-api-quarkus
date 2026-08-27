package com.example.claims.dto;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int offset,
        int limit,
        long total) {

    public static <T> PageResult<T> of(List<T> all, int offset, int limit) {
        int safeLimit = limit <= 0 ? 25 : limit;
        int safeOffset = Math.max(offset, 0);
        int from = Math.min(safeOffset, all.size());
        int to = Math.min(from + safeLimit, all.size());
        return new PageResult<>(all.subList(from, to), safeOffset, safeLimit, all.size());
    }
}
