package com.example.claims.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Customer(
        String id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Contact contact,
        String taxNumber,
        OffsetDateTime registeredAt) {

    public Customer withContact(Contact newContact) {
        return new Customer(id, firstName, lastName, birthDate, newContact, taxNumber, registeredAt);
    }
}
