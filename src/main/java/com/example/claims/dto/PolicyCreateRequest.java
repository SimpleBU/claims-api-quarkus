package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record PolicyCreateRequest(
        @NotBlank @Pattern(regexp = "^POL-[0-9]{10}$") String number,
        @NotBlank @Size(max = 40) String holderId,
        @NotNull LocalDate validFrom,
        @NotNull LocalDate validTo,
        @NotNull @Valid MoneyAmount premium,
        @NotEmpty @Size(max = 20) List<@Valid Coverage> coverages) {
}
