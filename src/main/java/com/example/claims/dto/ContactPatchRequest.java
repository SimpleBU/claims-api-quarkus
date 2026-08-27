package com.example.claims.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactPatchRequest(
        @Email @Size(max = 160) String email,
        @Pattern(regexp = "^\\+[0-9]{10,15}$") String phone,
        @Size(max = 200) String preferredChannel) {
}
