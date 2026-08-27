package com.example.claims.dto;

public record PurgeResult(
        String key,
        boolean purged,
        int remainingEntries) {
}
