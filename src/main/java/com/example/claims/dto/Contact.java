package com.example.claims.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Contact(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Pattern(regexp = "^\\+[0-9]{10,15}$") String phone,
        @Size(max = 200) String preferredChannel) {
}
