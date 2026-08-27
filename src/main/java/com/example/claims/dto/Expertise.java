package com.example.claims.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Expertise(
        String id,
        String claimId,
        String expertName,
        String organisation,
        LocalDate inspectionDate,
        VerdictType verdict,
        MoneyAmount assessedAmount,
        String reportFileName,
        OffsetDateTime createdAt) {

    public Expertise withVerdict(VerdictType newVerdict, MoneyAmount amount) {
        return new Expertise(id, claimId, expertName, organisation, inspectionDate, newVerdict, amount,
                reportFileName, createdAt);
    }

    public Expertise withReport(String fileName) {
        return new Expertise(id, claimId, expertName, organisation, inspectionDate, verdict, assessedAmount,
                fileName, createdAt);
    }
}
