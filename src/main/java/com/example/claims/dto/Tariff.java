package com.example.claims.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Tariff(
        String id,
        String code,
        String title,
        ClaimType appliesTo,
        BigDecimal baseRate,
        MoneyAmount minimumPremium,
        LocalDate effectiveFrom,
        boolean active) {

    public Tariff withRate(BigDecimal newRate, MoneyAmount newMinimum) {
        return new Tariff(id, code, title, appliesTo, newRate, newMinimum, effectiveFrom, active);
    }
}
