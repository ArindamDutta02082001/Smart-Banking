package com.royal.reserve.bank.transaction.api.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.reserve.bank.transaction.api.controller.TransactionController;
import com.royal.reserve.bank.transaction.api.dto.TransactionRequest;
import com.royal.reserve.bank.transaction.api.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Currency;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link TransactionController}.
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testProcessTransactionSuccess() throws Exception {
        // Given
        TransactionRequest request = createTransactionRequest();
        String jsonRequest = objectMapper.writeValueAsString(request);

        Mockito.when(transactionService.processTransaction(any(TransactionRequest.class)))
                .thenReturn("Transaction is processed");

        // When & Then
        mockMvc.perform(post("/api/transaction/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    void testProcessTransactionFallback() throws Exception {
        // Given
        TransactionRequest request = createTransactionRequest();
        String jsonRequest = objectMapper.writeValueAsString(request);

        // Simulate an exception in service
        Mockito.when(transactionService.processTransaction(any(TransactionRequest.class)))
                .thenThrow(new RuntimeException("Simulated exception"));

        // When & Then
        mockMvc.perform(post("/api/transaction/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());  // Because fallback also returns 201
    }

    private TransactionRequest createTransactionRequest() {
        TransactionRequest request = new TransactionRequest();
        request.setSenderMob("9999999999");
        request.setReceiverMob("8888888888");
        request.setAmount(1000.0);
        request.setPurpose("Test Transaction");
        request.setCurrency(Currency.getInstance("USD"));
        return request;
    }
}
