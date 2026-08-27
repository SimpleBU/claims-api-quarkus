package com.example.claims.dto;

import java.time.OffsetDateTime;

public record ClaimDocument(
        String id,
        String claimId,
        DocumentKind kind,
        String fileName,
        String contentType,
        long sizeBytes,
        OffsetDateTime uploadedAt) {
}
