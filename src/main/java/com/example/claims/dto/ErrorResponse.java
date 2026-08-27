package com.example.claims.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        String resource,
        OffsetDateTime timestamp,
        List<String> violations) {
}
