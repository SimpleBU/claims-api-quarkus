package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record PolicyUpdateRequest(
        @NotNull PolicyStatus status,
        @NotNull LocalDate validTo,
        @NotNull @Valid MoneyAmount premium,
        @Size(max = 20) List<@Valid Coverage> coverages) {
}
