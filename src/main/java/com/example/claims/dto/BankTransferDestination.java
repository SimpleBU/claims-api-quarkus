package com.example.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BankTransferDestination(
        @NotBlank @Pattern(regexp = "^[0-9]{20}$") String accountNumber,
        @NotBlank @Pattern(regexp = "^[0-9]{9}$") String bankCode,
        @NotBlank @Size(max = 200) String beneficiaryName) implements PayoutDestination {

    @Override
    public String channel() {
        return "BANK_TRANSFER";
    }
}
