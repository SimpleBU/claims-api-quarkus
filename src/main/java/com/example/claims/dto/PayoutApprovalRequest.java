package com.example.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PayoutApprovalRequest(
        @NotBlank @Size(max = 120) String approver,
        @Size(max = 500) String comment) {
}
