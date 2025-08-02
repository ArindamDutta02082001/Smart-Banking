package com.royal.reserve.bank.account.api.service;

import com.royal.reserve.bank.account.api.model.Account;
import com.royal.reserve.bank.account.api.repository.AccountRepository;
import com.royal.reserve.bank.account.api.dto.AccountResponse;
import com.royal.reserve.bank.account.api.dto.AccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class that provides operations for managing bank accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {


    private final AccountRepository accountRepository;

    private final RedisTemplate<String, List<AccountResponse>> redisTemplate;

    private static final String CACHE_KEY = "accounts";

    private static final Random random = new Random();

    /**
     * Creates a new bank account for a user with details in it.
     *
     * @param accountRequest The account request containing account details.
     */
    public Account createAccount(AccountRequest accountRequest) {


        Account account = Account.builder()
                .accountNumber(generateIBAN())
                .accountHolderName(accountRequest.getAccountHolderName())
                .balance(accountRequest.getBalance())
                .currency(accountRequest.getCurrency())
                .email(accountRequest.getEmail())
                .mobile(accountRequest.getMobile())
                .password(accountRequest.getPassword())
                .build();

        accountRepository.save(account);

        redisTemplate.delete(CACHE_KEY);

        log.info("Account for {} is created", account.getAccountHolderName());
        log.info("Your Smart Bank account id is : {} ", account.getAccountNumber());

        return account;
    }

    /**
     * utility function
     * Generates a random International Bank Account Number (IBAN).
     *
     * @return The generated IBAN.
     */
    public static String generateIBAN() {
        String[] countryCodes = Locale.getISOCountries();
        int index = random.nextInt(countryCodes.length);
        String countryCode = countryCodes[index];
        String accountNumber = String.format("%02d-%04d-%04d-%04d-%04d",
                random.nextInt(100),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
        return countryCode + accountNumber;
    }

    /**
     * Retrieves all bank accounts.
     *
     * @return A list of AccountResponse objects representing the bank accounts.
     */
    public List<AccountResponse> getAllAccounts() {
        List<AccountResponse> cachedAccounts = redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedAccounts != null) {
            return cachedAccounts;
        } else {
            List<Account> accounts = accountRepository.findAll();

            List<AccountResponse> accountResponses = accounts.stream()
                    .map(this::mapToAccountResponse)
                    .toList();
            redisTemplate.opsForValue().set(CACHE_KEY, accountResponses);
            return accountResponses;
        }
    }

    /**
     * utility function
     * Maps an Account object to an AccountResponse object.
     *
     * @param account The Account object to map.
     * @return The mapped AccountResponse object.
     */
    private AccountResponse mapToAccountResponse(Account account) {
       return AccountResponse.builder()
                .account(account)
                .message("active")
                .build();
    }

    /**
     * Deletes a bank account based on the account holder name.
     *
     * @param name The account holder name.
     * @throws NoSuchElementException if the account is not found.
     */
    public Optional<Account> deleteAccountByAccountHolderName(String name) {
        List<Account> accounts = accountRepository.findAll();

        Optional<Account> accountToDelete = accounts.stream()
                .filter(a -> a.getAccountHolderName() != null &&
                        a.getAccountHolderName().equals(name)).findFirst();

        if (accountToDelete.isPresent()) {
            accountRepository.delete(accountToDelete.get());
            redisTemplate.delete(CACHE_KEY);
            return accountToDelete;
        } else {
            throw new NoSuchElementException("The bank account information for "
                    + name + " was not found.");
        }
    }


    // delte all accnts
    public void deleteAllAccounts() {
        accountRepository.deleteAll();
    }
}
