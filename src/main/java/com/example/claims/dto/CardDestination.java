package com.example.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CardDestination(
        @NotBlank @Pattern(regexp = "^[0-9]{4}$") String last4,
        @NotBlank @Pattern(regexp = "^(VISA|MASTERCARD|MIR)$") String scheme,
        @NotBlank @Pattern(regexp = "^[A-Z]{2}$") String issuerCountry) implements PayoutDestination {

    @Override
    public String channel() {
        return "CARD";
    }
}
