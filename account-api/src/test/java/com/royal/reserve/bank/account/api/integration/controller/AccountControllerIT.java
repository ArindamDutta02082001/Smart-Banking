package com.royal.reserve.bank.account.api.integration.controller;

import com.royal.reserve.bank.account.api.controller.AccountController;
import com.royal.reserve.bank.account.api.dto.AccountRequest;
import com.royal.reserve.bank.account.api.dto.AccountResponse;
import com.royal.reserve.bank.account.api.model.Account;
import com.royal.reserve.bank.account.api.repository.AccountRepository;
import com.royal.reserve.bank.account.api.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for the {@link AccountController} class.
 */
@WebMvcTest(AccountController.class)
class AccountControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private AccountRepository accountRepository;

    /**
     * Test for the {@link AccountController#createAccount(AccountRequest)} method.
     *
     * @throws Exception if an exception occurs during the test
     */
    @Test
    void testCreateAccount() throws Exception {
        // Given
        Account account1 = createAccount("1234567","MT23-3821-4829-3279-9231", "George Clooney",
                BigDecimal.valueOf(1000), Currency.getInstance("USD"), "1234567890", "random@email.com", "password@123");

        when(accountService.createAccount(any(AccountRequest.class)))
                .thenReturn(account1);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountHolderName\":\"George Clooney\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.message").value("Successfully set up a new bank account for George Clooney."))
                .andExpect(jsonPath("$.account.accountHolderName").value("George Clooney"));
    }

    /**
     * Test for the {@link AccountController#getAllAccounts()} method.
     *
     * @throws Exception if an exception occurs during the test
     */
    @Test
    void testGetAllAccounts() throws Exception {
        // Given
        AccountResponse accountResponse1 = new AccountResponse();
        Account account1 = createAccount("14312415", "BG32-8472-4491-3894-2941", "Emma Stone",
                BigDecimal.valueOf(283000), Currency.getInstance("USD"), "1234567890", "emma@example.com", "password@123");
        accountResponse1.setAccount(account1);
        accountResponse1.setMessage("active");

        AccountResponse accountResponse2 = new AccountResponse();
        Account account2 = createAccount("94137274", "MT23-3821-4829-3279-9231", "Jennifer Lawrence",
                BigDecimal.valueOf(11400), Currency.getInstance("EUR"), "1234567890", "jennifer@example.com", "password@123");
        accountResponse2.setAccount(account2);
        accountResponse2.setMessage("active");

        List<AccountResponse> mockResponse = Arrays.asList(accountResponse1, accountResponse2);
        when(accountService.getAllAccounts()).thenReturn(mockResponse);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/account")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].account.id").value("14312415"))
                .andExpect(jsonPath("$[0].account.accountNumber").value("BG32-8472-4491-3894-2941"))
                .andExpect(jsonPath("$[0].account.accountHolderName").value("Emma Stone"))
                .andExpect(jsonPath("$[0].account.balance").value(283000))
                .andExpect(jsonPath("$[0].account.currency").value("USD"))
                .andExpect(jsonPath("$[1].account.id").value("94137274"))
                .andExpect(jsonPath("$[1].account.accountNumber").value("MT23-3821-4829-3279-9231"))
                .andExpect(jsonPath("$[1].account.accountHolderName").value("Jennifer Lawrence"))
                .andExpect(jsonPath("$[1].account.balance").value(11400))
                .andExpect(jsonPath("$[1].account.currency").value("EUR"));
    }

    /**
     * Test for the {@link AccountController#deleteAccount(AccountRequest)} method.
     *
     * @throws Exception if an exception occurs during the test
     */
    @Test
    void testDeleteAccount() throws Exception {
        // Given
        Account mockAccount = new Account();
        mockAccount.setAccountHolderName("Nicole Kidman");
        when(accountService.deleteAccountByAccountHolderName("Nicole Kidman"))
                .thenReturn(Optional.of(mockAccount));

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountHolderName\":\"Nicole Kidman\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Successfully deleted Nicole Kidman's account."));
    }

    private Account createAccount(String id, String accountNumber, String accountHolderName,
                                  BigDecimal balance, Currency currency, String mobile, String email, String password) {
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
