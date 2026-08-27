package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CustomerCreateRequest(
        @NotBlank @Size(min = 2, max = 80) String firstName,
        @NotBlank @Size(min = 2, max = 80) String lastName,
        @NotNull LocalDate birthDate,
        @NotNull @Valid Contact contact,
        @Pattern(regexp = "^[0-9]{12}$") String taxNumber) {
}
