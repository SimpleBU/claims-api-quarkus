package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerdictRequest(
        @NotNull VerdictType verdict,
        @NotNull @Valid MoneyAmount assessedAmount,
        @Size(max = 2000) String rationale) {
}
