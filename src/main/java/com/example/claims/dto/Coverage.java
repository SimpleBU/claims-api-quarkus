package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Coverage(
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 160) String title,
        @NotNull @Valid MoneyAmount limit,
        @NotNull @Valid MoneyAmount deductible) {
}
