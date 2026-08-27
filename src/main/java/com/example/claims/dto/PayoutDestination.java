package com.example.claims.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Settlement destination of a payout.
 *
 * <p>A single flat record instead of a type hierarchy: {@code channel} selects which group
 * of fields is meaningful. For {@code BANK_TRANSFER} that is {@code accountNumber},
 * {@code bankCode} and {@code beneficiaryName}; for {@code CARD} it is {@code last4},
 * {@code scheme} and {@code issuerCountry}. The fields of the other group stay null.
 */
public record PayoutDestination(
        @NotNull PayoutChannel channel,
        @Pattern(regexp = "^[0-9]{20}$") String accountNumber,
        @Pattern(regexp = "^[0-9]{9}$") String bankCode,
        @Size(max = 200) String beneficiaryName,
        @Pattern(regexp = "^[0-9]{4}$") String last4,
        @Pattern(regexp = "^(VISA|MASTERCARD|MIR)$") String scheme,
        @Pattern(regexp = "^[A-Z]{2}$") String issuerCountry) {
}
