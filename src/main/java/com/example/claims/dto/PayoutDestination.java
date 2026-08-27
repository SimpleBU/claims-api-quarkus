package com.example.claims.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "channel")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankTransferDestination.class, name = "BANK_TRANSFER"),
        @JsonSubTypes.Type(value = CardDestination.class, name = "CARD")
})
public sealed interface PayoutDestination permits BankTransferDestination, CardDestination {

    String channel();
}
