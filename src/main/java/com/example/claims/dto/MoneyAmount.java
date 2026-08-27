package com.example.claims.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record MoneyAmount(
        @NotNull @DecimalMin("0.00") BigDecimal value,
        @NotNull @Pattern(regexp = "^[A-Z]{3}$") String currency) {

    public static MoneyAmount rub(String value) {
        return new MoneyAmount(new BigDecimal(value), "RUB");
    }

    public MoneyAmount plus(MoneyAmount other) {
        return new MoneyAmount(value.add(other.value()), currency);
    }
}
