package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PayoutCreateRequest(
        @NotBlank @Size(max = 40) String claimId,
        @NotNull @Valid MoneyAmount amount,
        @NotNull @Valid PayoutDestination destination) {
}
