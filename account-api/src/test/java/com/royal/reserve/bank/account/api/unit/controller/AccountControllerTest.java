package com.royal.reserve.bank.account.api.unit.controller;

import com.royal.reserve.bank.account.api.controller.AccountController;
import com.royal.reserve.bank.account.api.dto.AccountRequest;
import com.royal.reserve.bank.account.api.dto.AccountResponse;
import com.royal.reserve.bank.account.api.model.Account;
import com.royal.reserve.bank.account.api.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link AccountController} class.
 */
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;


    /**
     * Test for the {@link AccountController#getAllAccounts()} method.
     */
    @Test
    void testGetAllAccounts() {
        // Given
        AccountResponse accountResponse1 = new AccountResponse();
        Account account1 = createAccount("MT23-3821-4829-3279-9231", "Tom Hanks",
                BigDecimal.valueOf(1000), Currency.getInstance("USD") , "1234567890" , "random@email.com", "password@123");
        accountResponse1.setAccount(account1);
        accountResponse1.setMessage("active");

        AccountResponse accountResponse2 = new AccountResponse();
        Account account2 = createAccount("DE32-8473-8127-1823-1732", "Julia Roberts",
                BigDecimal.valueOf(1000), Currency.getInstance("USD") , "1234567890" , "random@email.com", "password@123");
        accountResponse1.setAccount(account2);
        accountResponse1.setMessage("active");

        List<AccountResponse> expectedAccounts = Arrays.asList(accountResponse1, accountResponse2);

        when(accountService.getAllAccounts()).thenReturn(expectedAccounts);

        // When
        List<AccountResponse> actualAccounts = accountController.getAllAccounts();

        // Then
        assertEquals(expectedAccounts, actualAccounts);
        verify(accountService, times(1)).getAllAccounts();
    }


    private Account createAccount(String accountNumber, String accountHolderName,
                                  BigDecimal balance, Currency currency , String mobile , String email, String password) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountHolderName(accountHolderName);
        account.setBalance(balance);
        account.setCurrency(currency);
        account.setMobile(mobile);
        account.setEmail(email);
        account.setPassword(password);
        return account;
    }

    /**
     * Test for the {@link AccountController#deleteAccount(AccountRequest)} method.
     */
    @Test
    void testDeleteAccount() {
        // Given
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountHolderName("Al Pacino");

        // When
        ResponseEntity<String> responseEntity = accountController.deleteAccount(accountRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successfully deleted Al Pacino's account.", responseEntity.getBody());
        verify(accountService, times(1)).deleteAccountByAccountHolderName("Al Pacino");
    }
}