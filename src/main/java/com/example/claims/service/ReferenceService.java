package com.example.claims.service;

import com.example.claims.dto.ClaimType;
import com.example.claims.dto.ReferenceItem;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ReferenceService {

    private static final List<ReferenceItem> CURRENCIES = List.of(
            new ReferenceItem("RUB", "Russian rouble", "Settlement currency for domestic policies"),
            new ReferenceItem("EUR", "Euro", "Used for travel policies within the EU"),
            new ReferenceItem("USD", "US dollar", "Used for international liability policies"));

    private static final List<ReferenceItem> REGIONS = List.of(
            new ReferenceItem("CFO", "Central federal district", "Moscow and the surrounding regions"),
            new ReferenceItem("SZFO", "North-western federal district", "Saint Petersburg and the north-west"),
            new ReferenceItem("PFO", "Volga federal district", "Kazan, Samara, Nizhny Novgorod"));

    public List<ReferenceItem> currencies() {
        return CURRENCIES;
    }

    public List<ReferenceItem> regions() {
        return REGIONS;
    }

    public List<ReferenceItem> claimTypes() {
        return Arrays.stream(ClaimType.values())
                .map(type -> new ReferenceItem(type.name(), humanise(type),
                        "Claim category used by the intake form"))
                .toList();
    }

    private String humanise(ClaimType type) {
        String[] parts = type.name().toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return result.toString();
    }
}
