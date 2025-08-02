package com.royal.reserve.bank.transaction.api.unit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.royal.reserve.bank.transaction.api.controller.TransactionController;
import com.royal.reserve.bank.transaction.api.dto.TransactionRequest;
import com.royal.reserve.bank.transaction.api.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TransactionController}.
 */
@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionRequest request;

    @BeforeEach
    void setup() {
        request = new TransactionRequest();
        request.setSenderMob("9999999999");
        request.setReceiverMob("8888888888");
        request.setAmount(5000.0);
        request.setPurpose("Test payment");
        request.setCurrency(Currency.getInstance("USD"));
    }

    /**
     * Test for {@link TransactionController#processTransaction(TransactionRequest)}.
     */
    @Test
    void testProcessTransaction() throws JsonProcessingException {
        // Given
        String expectedResponse = "Transaction is processed";
        when(transactionService.processTransaction(any(TransactionRequest.class))).thenReturn(expectedResponse);

        // When
        CompletableFuture<String> result = transactionController.processTransaction(request);

        // Then
        assertEquals(expectedResponse, result.join());
    }

    /**
     * Test for {@link TransactionController#fallbackMethod(TransactionRequest, RuntimeException)}.
     */
    @Test
    void testFallbackMethod() {
        // Given
        RuntimeException exception = new RuntimeException("Simulated failure");
        String expectedFallback = "Oops! Something went wrong, please try again later!";

        // When
        CompletableFuture<String> result = transactionController.fallbackMethod(request, exception);

        // Then
        assertEquals(expectedFallback, result.join());
    }
}
