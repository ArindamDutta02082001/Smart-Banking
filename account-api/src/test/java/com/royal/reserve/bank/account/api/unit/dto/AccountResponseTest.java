package com.royal.reserve.bank.account.api.unit.dto;

import com.royal.reserve.bank.account.api.dto.AccountResponse;
import com.royal.reserve.bank.account.api.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Unit tests for the {@link AccountResponse} class.
 */
class AccountResponseTest {

    /**
     * Test the constructors.
     */
    @Test
    void testAccountResponse() {
        // Given
        String expectedId = "52342";
        String expectedAccountNumber = "BN12-4123-1235-7653-8576";
        String expectedAccountHolderName = "Nelson Mandela";
        BigDecimal expectedBalance = new BigDecimal("610345.0");
        Currency expectedCurrency = Currency.getInstance("EUR");

        // When
        AccountResponse accountResponse1 = new AccountResponse();

        Account account1 = createAccount("52342", "BN12-4123-1235-7653-8576", "Nelson Mandela",
                BigDecimal.valueOf(610345.0), Currency.getInstance("EUR") , "1234567890" , "random@email.com", "password@123");
        accountResponse1.setAccount(account1);
        accountResponse1.setMessage("active");

        Account response = accountResponse1.getAccount();

                // Then
        Assertions.assertEquals(expectedId, response.getId());
        Assertions.assertEquals(expectedAccountNumber, response.getAccountNumber());
        Assertions.assertEquals(expectedAccountHolderName, response.getAccountHolderName());
        Assertions.assertEquals(expectedBalance, response.getBalance());
        Assertions.assertEquals(expectedCurrency, response.getCurrency());
    }

    private Account createAccount(String id , String accountNumber, String accountHolderName,
                                  BigDecimal balance, Currency currency , String mobile , String email, String password) {
        Account account = new Account();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setAccountHolderName(accountHolderName);
        account.setBalance(balance);
        account.setCurrency(currency);
        account.setMobile(mobile);
        account.setEmail(email);
        account.setPassword(password);
        return account;
    }
}
