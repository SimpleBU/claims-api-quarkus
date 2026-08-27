package com.example.claims.dto;

public record DocumentUploadResult(
        ClaimDocument document,
        String storageLocation,
        String checksum) {
}
