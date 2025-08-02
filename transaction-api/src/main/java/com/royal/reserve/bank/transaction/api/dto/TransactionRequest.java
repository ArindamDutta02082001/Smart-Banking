package com.royal.reserve.bank.transaction.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;
import java.util.List;

/**
 * Data Transfer Object (DTO) class that represents the request payload for a transaction.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    // for starting a transaction we need the receiver user and amount and purpose

    @NotBlank
    private String senderMob; // phone number

    @NotBlank
    private String receiverMob; // phone number

    @NotNull
    private Double amount;

    private String purpose;

    private Currency Currency;
}
