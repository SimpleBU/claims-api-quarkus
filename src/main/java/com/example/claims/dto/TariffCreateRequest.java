package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TariffCreateRequest(
        @NotBlank @Pattern(regexp = "^TRF-[A-Z0-9]{4,10}$") String code,
        @NotBlank @Size(max = 160) String title,
        @NotNull ClaimType appliesTo,
        @NotNull @DecimalMin("0.001") @DecimalMax("1.000") BigDecimal baseRate,
        @NotNull @Valid MoneyAmount minimumPremium,
        @NotNull LocalDate effectiveFrom) {
}
