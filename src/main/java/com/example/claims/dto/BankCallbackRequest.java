package com.example.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BankCallbackRequest(
        @NotBlank String externalReference,
        @NotNull PayoutStatus status,
        @NotBlank String signature) {
}
