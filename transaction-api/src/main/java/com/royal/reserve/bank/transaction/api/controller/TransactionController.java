package com.royal.reserve.bank.transaction.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.royal.reserve.bank.transaction.api.dto.TransactionRequest;
import com.royal.reserve.bank.transaction.api.model.Transaction;
import com.royal.reserve.bank.transaction.api.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class that handles HTTP requests related to transactions.
 */
@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     *
     *Processes a transaction asynchronously.
     *@param transactionRequest The transaction request object received in the request body.
     *@return A CompletableFuture representing the result of the transaction processing.
     */
    @PostMapping("/initiate")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "asset-management", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "asset-management")
    @Retry(name = "asset-management")
    public CompletableFuture<String> processTransaction(@RequestBody TransactionRequest transactionRequest) {
        log.info("Transaction processed.");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return transactionService.processTransaction(transactionRequest);
            } catch (JsonProcessingException e) {
               return "Transaction error : "+e.getMessage();
            }
        });
    }

    /**
     *
     *Circuit breaker implementation. Fallback method to handle exceptions during transaction processing.
     *@param transactionRequest The transaction request object.
     *@param runtimeException The exception that occurred during transaction processing.
     *@return A CompletableFuture representing a fallback message.
     */
    public CompletableFuture<String> fallbackMethod(TransactionRequest transactionRequest, RuntimeException runtimeException) {
        log.info("Transaction can't be processed. Executing fallback logic.");
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please try again later!");
    }

    @GetMapping("/txn-history/{sender-mobile}")
    public List<Transaction> allTransactionDone(@PathVariable("sender-mobile") String mobile)
    {
        return transactionService.getAllTransaction(mobile);
    }
}