package com.example.claims.dto;

import java.time.OffsetDateTime;

public record Payout(
        String id,
        String claimId,
        PayoutStatus status,
        MoneyAmount amount,
        PayoutDestination destination,
        String approvedBy,
        String externalReference,
        OffsetDateTime createdAt) {

    public Payout withStatus(PayoutStatus newStatus, String approver) {
        return new Payout(id, claimId, newStatus, amount, destination, approver, externalReference, createdAt);
    }

    public Payout withAmountAndDestination(MoneyAmount newAmount, PayoutDestination newDestination) {
        return new Payout(id, claimId, status, newAmount, newDestination, approvedBy, externalReference,
                createdAt);
    }
}
