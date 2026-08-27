package com.example.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ExpertiseCreateRequest(
        @NotBlank @Size(max = 40) String claimId,
        @NotBlank @Size(max = 160) String expertName,
        @NotBlank @Size(max = 160) String organisation,
        @NotNull LocalDate inspectionDate) {
}
