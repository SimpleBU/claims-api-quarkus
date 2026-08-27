package com.example.claims.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Claim(
        String id,
        String claimNumber,
        String policyId,
        String customerId,
        ClaimType type,
        ClaimStatus status,
        LocalDate incidentDate,
        String description,
        MoneyAmount claimedAmount,
        MoneyAmount approvedAmount,
        OffsetDateTime registeredAt,
        OffsetDateTime updatedAt) {

    public Claim withStatus(ClaimStatus newStatus) {
        return new Claim(id, claimNumber, policyId, customerId, type, newStatus, incidentDate, description,
                claimedAmount, approvedAmount, registeredAt, OffsetDateTime.now());
    }

    public Claim withApprovedAmount(MoneyAmount amount) {
        return new Claim(id, claimNumber, policyId, customerId, type, status, incidentDate, description,
                claimedAmount, amount, registeredAt, OffsetDateTime.now());
    }
}
