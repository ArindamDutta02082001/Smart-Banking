package com.royal.reserve.bank.transaction.api.dto.FeignClientResponse;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * Represents a bank account.
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private Currency currency;


    @Column(nullable = false, unique = true)
    private String mobile;

    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;
}