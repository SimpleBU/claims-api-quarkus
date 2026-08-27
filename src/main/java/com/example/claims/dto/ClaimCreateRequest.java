package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClaimCreateRequest(
        @NotBlank @Size(max = 40) String policyId,
        @NotBlank @Size(max = 40) String customerId,
        @NotNull ClaimType type,
        @NotNull LocalDate incidentDate,
        @NotBlank @Size(min = 20, max = 4000) String description,
        @NotNull @Valid MoneyAmount claimedAmount) {
}
