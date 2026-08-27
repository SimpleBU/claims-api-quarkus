package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TariffUpdateRequest(
        @NotNull @DecimalMin("0.001") @DecimalMax("1.000") BigDecimal baseRate,
        @NotNull @Valid MoneyAmount minimumPremium,
        @NotNull Boolean active) {
}
