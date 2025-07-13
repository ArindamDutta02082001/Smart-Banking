package com.royal.reserve.bank.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Data Transfer Object (DTO) class that represents the request payload for a bank account holder.
 * We are using @Schema to show details of each fields in the swagger html page
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating an account")
public class AccountRequest {

    @Schema(description = "Name of the account holder", example = "John Doe")
    private String accountHolderName;

    @Schema(description = "Initial balance", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Currency details")
    private Currency currency;

    // "currency": "EUR" "INR"
}
