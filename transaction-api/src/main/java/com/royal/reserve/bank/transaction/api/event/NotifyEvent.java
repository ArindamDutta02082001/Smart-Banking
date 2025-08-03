package com.royal.reserve.bank.transaction.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

/**
 * Represents a notify  event between Transaction API and Notification API.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyEvent {
    private String transactionId;

    private String senderName;
    private String receiverName;

    private String senderMob;
    private String receiverMob;

    private String senderMail;
    private String receiverMail;

    private String Message;

    private java.util.Currency Currency;

    private int amt;


}

