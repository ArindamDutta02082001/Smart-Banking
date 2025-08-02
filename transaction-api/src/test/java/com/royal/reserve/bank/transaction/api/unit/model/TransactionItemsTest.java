package com.royal.reserve.bank.transaction.api.unit.model;

import com.royal.reserve.bank.transaction.api.model.TransactionItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link TransactionItems} class.
 */
class TransactionItemsTest {

    private TransactionItems transactionItems;

    @BeforeEach
    void setUp() {
        transactionItems = new TransactionItems();
        transactionItems.setId(1L);
        transactionItems.setMessage("Investment Transfer");
        transactionItems.setAmt(5000);
        transactionItems.setCurrency(Currency.getInstance("USD"));
    }

    @Test
    void testGetId() {
        assertEquals(1L, transactionItems.getId());
    }

    @Test
    void testGetMessage() {
        assertEquals("Investment Transfer", transactionItems.getMessage());
    }

    @Test
    void testGetAmt() {
        assertEquals(5000, transactionItems.getAmt());
    }

    @Test
    void testGetCurrency() {
        assertEquals(Currency.getInstance("USD"), transactionItems.getCurrency());
    }

    @Test
    void testSetId() {
        transactionItems.setId(2L);
        assertEquals(2L, transactionItems.getId());
    }

    @Test
    void testSetMessage() {
        transactionItems.setMessage("Updated Message");
        assertEquals("Updated Message", transactionItems.getMessage());
    }

    @Test
    void testSetAmt() {
        transactionItems.setAmt(9999);
        assertEquals(9999, transactionItems.getAmt());
    }

    @Test
    void testSetCurrency() {
        Currency newCurrency = Currency.getInstance("INR");
        transactionItems.setCurrency(newCurrency);
        assertEquals(newCurrency, transactionItems.getCurrency());
    }
}
