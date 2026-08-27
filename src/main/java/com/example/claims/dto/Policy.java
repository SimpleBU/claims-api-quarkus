package com.example.claims.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record Policy(
        String id,
        String number,
        String holderId,
        PolicyStatus status,
        LocalDate validFrom,
        LocalDate validTo,
        MoneyAmount premium,
        List<Coverage> coverages,
        OffsetDateTime updatedAt) {

    public Policy withStatus(PolicyStatus newStatus) {
        return new Policy(id, number, holderId, newStatus, validFrom, validTo, premium, coverages,
                OffsetDateTime.now());
    }
}
