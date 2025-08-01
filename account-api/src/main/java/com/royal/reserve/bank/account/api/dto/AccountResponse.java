package com.royal.reserve.bank.account.api.dto;

import com.royal.reserve.bank.account.api.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Data Transfer Object (DTO) class that represents the response payload for a bank account holder.
 * We are using @Schema to show details of each fields in the swagger html page
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for account operations")
public class AccountResponse {
    private String message;
    private Account account;
}
