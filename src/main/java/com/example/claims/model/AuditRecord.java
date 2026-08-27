package com.example.claims.model;

import java.time.Instant;

public record AuditRecord(String actor, String action, String subject, Instant at) {

    public static AuditRecord now(String actor, String action, String subject) {
        return new AuditRecord(actor, action, subject, Instant.now());
    }
}
