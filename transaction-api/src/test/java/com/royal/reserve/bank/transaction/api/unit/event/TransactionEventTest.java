package com.royal.reserve.bank.transaction.api.unit.event;

import com.royal.reserve.bank.transaction.api.event.TransactionEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Currency;

/**
 * Unit tests for the {@link TransactionEvent} class.
 */
class TransactionEventTest {

    /**
     * Test full constructor and getters.
     */
    @Test
    void testTransactionEventAllArgsConstructor() {
        // Given
        String transactionId = "876463547";
        String senderName = "John Doe";
        String receiverName = "Jane Smith";
        String senderMob = "1234567890";
        String receiverMob = "0987654321";
        String senderMail = "john@example.com";
        String receiverMail = "jane@example.com";
        String message = "Payment for services";
        Currency currency = Currency.getInstance("USD");
        int amount = 5000;

        // When
        TransactionEvent event = new TransactionEvent(
                transactionId, senderName, receiverName,
                senderMob, receiverMob,
                message, currency, amount
        );

        // Then
        Assertions.assertEquals(transactionId, event.getTransactionId());
        Assertions.assertEquals(senderName, event.getSenderName());
        Assertions.assertEquals(receiverName, event.getReceiverName());
        Assertions.assertEquals(senderMob, event.getSenderMob());
        Assertions.assertEquals(receiverMob, event.getReceiverMob());
        Assertions.assertEquals(message, event.getMessage());
        Assertions.assertEquals(currency, event.getCurrency());
        Assertions.assertEquals(amount, event.getAmt());
    }

    /**
     * Test no-args constructor and setters.
     */
    @Test
    void testTransactionEventNoArgsConstructorAndSetters() {
        // Given
        TransactionEvent event = new TransactionEvent();

        String transactionId = "12345";
        String senderName = "Alice";
        String receiverName = "Bob";
        String senderMob = "1111111111";
        String receiverMob = "2222222222";
        String senderMail = "alice@example.com";
        String receiverMail = "bob@example.com";
        String message = "Invoice Payment";
        Currency currency = Currency.getInstance("INR");
        int amount = 7000;

        // When
        event.setTransactionId(transactionId);
        event.setSenderName(senderName);
        event.setReceiverName(receiverName);
        event.setSenderMob(senderMob);
        event.setReceiverMob(receiverMob);
        event.setMessage(message);
        event.setCurrency(currency);
        event.setAmt(amount);

        // Then
        Assertions.assertEquals(transactionId, event.getTransactionId());
        Assertions.assertEquals(senderName, event.getSenderName());
        Assertions.assertEquals(receiverName, event.getReceiverName());
        Assertions.assertEquals(senderMob, event.getSenderMob());
        Assertions.assertEquals(receiverMob, event.getReceiverMob());
        Assertions.assertEquals(message, event.getMessage());
        Assertions.assertEquals(currency, event.getCurrency());
        Assertions.assertEquals(amount, event.getAmt());
    }
}
