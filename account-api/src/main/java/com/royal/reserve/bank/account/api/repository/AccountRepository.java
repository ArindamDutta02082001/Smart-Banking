package com.royal.reserve.bank.account.api.repository;

import com.royal.reserve.bank.account.api.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for storing bank accounts in the MongoDB database.
 * It provides CRUD operations and other database-related functionality.
 */
public interface AccountRepository extends MongoRepository<Account, String> {

    Optional<Account> findByAccountHolderNameAndMobile(String accountHolderName, String mobile);

}
