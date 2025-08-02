package com.royal.reserve.bank.account.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.reserve.bank.account.api.event.UserEvent;
import com.royal.reserve.bank.account.api.model.Account;
import com.royal.reserve.bank.account.api.repository.AccountRepository;
import com.royal.reserve.bank.account.api.service.AccountService;
import com.royal.reserve.bank.account.api.dto.AccountResponse;
import com.royal.reserve.bank.account.api.dto.AccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller class that handles HTTP requests related to bank accounts.
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Autowired
    private final AccountRepository accountRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;   // topic , event

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Creates a new bank account.
     *
     * @param accountRequest The account request containing account details.
     * @return A ResponseEntity with a success message and HTTP status code 201 if the account was created successfully.
     */
    @PostMapping
    @Operation(summary="create a new user account")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest accountRequest) throws JsonProcessingException {

        // we will find if a account with same name and phone number exists
        Optional<Account> existingAccount = accountRepository
                .findByAccountHolderNameAndMobile(accountRequest.getAccountHolderName(), accountRequest.getMobile());

        if (existingAccount.isPresent()) {
            AccountResponse accountResponse = AccountResponse.builder()
                    .account(existingAccount.get())
                    .message("Already account exists for " +
                            accountRequest.getAccountHolderName() + " with mobile : " + accountRequest.getMobile())
                    .build();


            return ResponseEntity.status(HttpStatus.CREATED).body
                    (accountResponse);
        }

        Account  account = accountService.createAccount(accountRequest);

        AccountResponse accountResponse = AccountResponse.builder()
                .account(account)
                .message("Successfully set up a new bank account for " +
                        accountRequest.getAccountHolderName() + ".")
                                            .build();

        // pushing to kafka to create asset of the user
        String json = objectMapper.writeValueAsString(buildEvent(account, UserEvent.EventType.CREATED));
        kafkaTemplate.send("user.created", json);  // topic , event


        return ResponseEntity.status(HttpStatus.CREATED).body
                (accountResponse);
    }

    /**
     * It has to be protected . Only bank member can see
     * Retrieves all bank accounts.
     *
     * @return A list of AccountResponse objects representing the bank accounts.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary="return A list of AccountResponse objects representing the bank accounts.")
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    /**
     * Deletes a bank account based on the account holder name.
     *
     * @param accountRequest The account request containing the account holder name.
     * @return A ResponseEntity with a success message and HTTP status code 200 if the account was deleted successfully,
     *         or a ResponseEntity with an error message and HTTP status code 404 if the account was not found.
     */
    @DeleteMapping
    @Operation(summary="return A ResponseEntity with a success message and HTTP status code 200 if the account was deleted")
    public ResponseEntity<String> deleteAccount(@RequestBody AccountRequest accountRequest) {
        try {
            Optional<Account> account = accountService.deleteAccountByAccountHolderName(accountRequest.getAccountHolderName());

            if(account.isPresent()) {
                // pushing to kafka to delete the asset of the user
                String json = objectMapper.writeValueAsString(buildEvent(account.get(), UserEvent.EventType.DELETED));
                kafkaTemplate.send("user.deleted", json);  // topic , event
            }

            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted " +
                    accountRequest.getAccountHolderName() + "'s account.");
        } catch (RuntimeException | JsonProcessingException runtimeException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(runtimeException.getMessage());
        }
    }

    @DeleteMapping("/all")
    public String deleteAllAccounts()
    {
        accountService.deleteAllAccounts();
        return "all account deleted !! ";
    }


    // utility function to create event
    private UserEvent buildEvent(Account acc, UserEvent.EventType type) {
        return UserEvent.builder()
                .userId(acc.getId())
                .accountHolderName(acc.getAccountHolderName())
                .mobile(acc.getMobile())
                .email(acc.getEmail())
                .balance(acc.getBalance())
                .currency(acc.getCurrency())
                .type(type)
                .build();
    }
}
