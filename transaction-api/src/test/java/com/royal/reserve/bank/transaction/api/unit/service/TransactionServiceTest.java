package com.royal.reserve.bank.transaction.api.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.reserve.bank.transaction.api.client.AccountServiceClient;
import com.royal.reserve.bank.transaction.api.client.AssetManagementClient;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.Account;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.AccountServiceResponse;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.Asset;
import com.royal.reserve.bank.transaction.api.dto.TransactionRequest;
import com.royal.reserve.bank.transaction.api.model.Transaction;
import com.royal.reserve.bank.transaction.api.repository.TransactionRepository;
import com.royal.reserve.bank.transaction.api.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AssetManagementClient assetManagementClient;
    @Mock private AccountServiceClient accountServiceClient;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private TransactionService transactionService;

    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        transactionRequest = new TransactionRequest();
        transactionRequest.setSenderMob("9999999999");
        transactionRequest.setReceiverMob("8888888888");
        transactionRequest.setAmount(5000.0);
        transactionRequest.setPurpose("Birthday gift");
        transactionRequest.setCurrency(Currency.getInstance("INR"));
    }

    @Test
    void processTransaction_withValidAccountsAndFunds_shouldSucceed() throws Exception {
        // Arrange
        Account sender = Account.builder()
                .id("1")
                .accountNumber("SBIN0001")
                .accountHolderName("Alice")
                .balance(BigDecimal.valueOf(10000))
                .currency(Currency.getInstance("INR"))
                .mobile("9999999999")
                .email("alice@example.com")
                .password("password")
                .build();

        Account receiver = Account.builder()
                .id("2")
                .accountNumber("SBIN0002")
                .accountHolderName("Bob")
                .balance(BigDecimal.valueOf(2000))
                .currency(Currency.getInstance("INR"))
                .mobile("8888888888")
                .email("bob@example.com")
                .password("password")
                .build();

        when(accountServiceClient.getAllAccounts()).thenReturn(List.of(
                new AccountServiceResponse("ok", sender),
                new AccountServiceResponse("ok", receiver)
        ));

        Asset asset = new Asset();
        asset.setValue(6000);
        when(assetManagementClient.checkAssetAvailability("9999999999")).thenReturn(Optional.of(asset));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"mocked\":\"event\"}");

        // Act
        String result = transactionService.processTransaction(transactionRequest);

        // Assert
        assertEquals("Transaction is processed ", result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(kafkaTemplate, times(1)).send(eq("user.transaction"), anyString());
        verify(kafkaTemplate, times(1)).send(eq("user.notify"), anyString());
    }

    @Test
    void processTransaction_whenSenderMissing_shouldReturnSenderError() throws Exception {
        // Arrange
        Account receiver = Account.builder()
                .mobile("8888888888")
                .accountHolderName("Bob")
                .email("bob@example.com")
                .build();

        when(accountServiceClient.getAllAccounts()).thenReturn(List.of(
                new AccountServiceResponse("ok", receiver)
        ));

        // Act
        String result = transactionService.processTransaction(transactionRequest);

        // Assert
        assertEquals(" Sender dont have a Smart Bank Account", result);
        verify(transactionRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void processTransaction_whenInsufficientFund_shouldReturnError() throws Exception {
        // Arrange
        Account sender = Account.builder()
                .mobile("9999999999")
                .accountHolderName("Alice")
                .email("alice@example.com")
                .balance(BigDecimal.valueOf(10000))
                .currency(Currency.getInstance("INR"))
                .build();

        Account receiver = Account.builder()
                .mobile("8888888888")
                .accountHolderName("Bob")
                .email("bob@example.com")
                .balance(BigDecimal.valueOf(2000))
                .currency(Currency.getInstance("INR"))
                .build();

        when(accountServiceClient.getAllAccounts()).thenReturn(List.of(
                new AccountServiceResponse("ok", sender),
                new AccountServiceResponse("ok", receiver)
        ));

        Asset asset = new Asset();
        asset.setValue(1000); // Less than required
        when(assetManagementClient.checkAssetAvailability("9999999999")).thenReturn(Optional.of(asset));

        // Act
        String result = transactionService.processTransaction(transactionRequest);

        // Assert
        assertEquals("Insufficient fund for the sender", result);
        verify(transactionRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
