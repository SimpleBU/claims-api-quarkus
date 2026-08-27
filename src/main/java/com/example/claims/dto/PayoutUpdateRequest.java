package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PayoutUpdateRequest(
        @NotNull @Valid MoneyAmount amount,
        @NotNull @Valid PayoutDestination destination) {
}
