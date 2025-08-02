package com.royal.reserve.bank.notification.api.unit.event;


import com.royal.reserve.bank.notification.api.event.TransactionEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Currency;

/**
 * Unit tests for {@link TransactionEvent} class.
 */
class TransactionEventTest {

    @Test
    void testTransactionEventFullConstructor() {
        // Given
        String transactionId = "13645940";
        String senderName = "Alice";
        String receiverName = "Bob";
        String senderMob = "1111111111";
        String receiverMob = "2222222222";
        String senderMail = "alice@example.com";
        String receiverMail = "bob@example.com";
        String message = "Payment for rent";
        Currency currency = Currency.getInstance("USD");
        int amt = 100;

        // When
        TransactionEvent event = new TransactionEvent(
                transactionId, senderName, receiverName,
                senderMob, receiverMob, senderMail, receiverMail,
                message, currency, amt
        );

        // Then
        Assertions.assertEquals(transactionId, event.getTransactionId());
        Assertions.assertEquals(amt, event.getAmt());
        Assertions.assertEquals(currency, event.getCurrency());
    }

}

