package com.example.claims.dto;

import java.time.OffsetDateTime;

public record AckResult(
        String outcome,
        String reference,
        OffsetDateTime acknowledgedAt) {
}
