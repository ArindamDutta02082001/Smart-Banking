package com.royal.reserve.bank.transaction.api.unit.dto.FeignClientResponse;

import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.Account;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.AccountServiceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AccountServiceResponse} class.
 */
class AccountServiceResponseTest {

    /**
     * Test the builder, constructor, and getters.
     */
    @Test
    void testAccountServiceResponseBuilderAndGetters() {
        // Given
        String expectedMessage = "Account fetched successfully";
        Account expectedAccount = Account.builder()
                .accountHolderName("John Doe")
                .email("john.doe@example.com")
                .mobile("9876543210")
                .balance(new java.math.BigDecimal("2500.00"))
                .currency(java.util.Currency.getInstance("USD"))
                .build();

        // When
        AccountServiceResponse response = AccountServiceResponse.builder()
                .message(expectedMessage)
                .account(expectedAccount)
                .build();

        // Then
        Assertions.assertEquals(expectedMessage, response.getMessage());
        Assertions.assertEquals(expectedAccount, response.getAccount());
    }

    /**
     * Test no-args constructor and setters.
     */
    @Test
    void testAccountServiceResponseSetters() {
        // Given
        AccountServiceResponse response = new AccountServiceResponse();
        String expectedMessage = "Data returned";
        Account account = new Account();
        account.setAccountHolderName("Alice");
        account.setEmail("alice@example.com");
        account.setMobile("1234567890");

        // When
        response.setMessage(expectedMessage);
        response.setAccount(account);

        // Then
        Assertions.assertEquals(expectedMessage, response.getMessage());
        Assertions.assertEquals(account, response.getAccount());
    }
}
