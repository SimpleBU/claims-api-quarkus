package com.example.claims.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record ClaimPatchRequest(
        ClaimStatus status,
        @Size(max = 4000) String description,
        @Valid MoneyAmount approvedAmount) {
}
