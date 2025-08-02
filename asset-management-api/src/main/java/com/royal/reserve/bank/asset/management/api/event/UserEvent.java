package com.royal.reserve.bank.asset.management.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {

    private String userId;                // Unique ID of the user (assigned by DB or UUID)
    private String mobile;
    private String email;
    private String accountHolderName;
    private BigDecimal balance;
    private Currency currency;

    private EventType type;
    public enum EventType {
        CREATED, DELETED
    }


}
