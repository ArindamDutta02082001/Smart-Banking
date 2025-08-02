package com.royal.reserve.bank.transaction.api.unit.dto;

import com.royal.reserve.bank.transaction.api.dto.TransactionRequest;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link TransactionRequest} class.
 */
class TransactionRequestTest {

    /**
     * Test the constructor and getters/setters.
     */
    @Test
    void testTransactionRequest() {
        // Given
        String senderMob = "9999999999";
        String receiverMob = "8888888888";
        Double amount = 5000.0;
        String purpose = "Test Purpose";
        Currency currency = Currency.getInstance("USD");

        // When
        TransactionRequest request = new TransactionRequest();
        request.setSenderMob(senderMob);
        request.setReceiverMob(receiverMob);
        request.setAmount(amount);
        request.setPurpose(purpose);
        request.setCurrency(currency);

        // Then
        assertEquals(senderMob, request.getSenderMob());
        assertEquals(receiverMob, request.getReceiverMob());
        assertEquals(amount, request.getAmount());
        assertEquals(purpose, request.getPurpose());
        assertEquals(currency, request.getCurrency());
    }
}
