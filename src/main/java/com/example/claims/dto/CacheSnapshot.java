package com.example.claims.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record CacheSnapshot(
        Map<String, Integer> sizeByRegion,
        long lookups,
        OffsetDateTime collectedAt) {
}
