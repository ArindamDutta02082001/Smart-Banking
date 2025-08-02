package com.royal.reserve.bank.transaction.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Currency;

/**
 * Represents a transaction item.
 */
@Entity
@Table(name = "transaction_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Message;

    private int amt;

    private Currency Currency;
}
