package com.royal.reserve.bank.asset.management.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

/**
 * Represents a transaction event between Transaction API and Notification API.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String senderName;
    private String receiverName;

    private String senderMob;
    private String receiverMob;

    private String Message;
    private Currency Currency;

    private int amt;


}
