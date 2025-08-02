package com.royal.reserve.bank.asset.management.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.reserve.bank.asset.management.api.event.TransactionEvent;
import com.royal.reserve.bank.asset.management.api.event.UserEvent;
import com.royal.reserve.bank.asset.management.api.model.Asset;
import com.royal.reserve.bank.asset.management.api.repository.AssetManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class that provides operations for managing assets.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssetManagementService {

    private final AssetManagementRepository assetManagementRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Checks the availability of assets based on their codes.
     *
     * @return A list of AssetManagementResponse objects indicating the availability of each asset.
     */
    @Transactional(readOnly = true)
    @SneakyThrows
    @Cacheable("assetAvailability")
    public Optional<Asset> isAssetAvailable(String mobile) {
        log.info("Checking asset availability");
        return assetManagementRepository.findByMobile(mobile);
    }

    /**
     * Triggered when a new user account is created.
     * Initializes assets for the user.
     */
    @KafkaListener(topics = "user.created", groupId = "asset-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserCreated(String event) throws JsonProcessingException {
        log.info("Received user.created event for: {}", event);

        ObjectMapper mapper = new ObjectMapper();
        UserEvent userevent = mapper.readValue(event, UserEvent.class);

        Asset asset = Asset.builder()
                .assetCode("BANK_ACCOUNT") // example asset
                .assetName("Wallet")
                .value(userevent.getBalance().intValue())
                .UserId(userevent.getUserId())
                .mobile(userevent.getMobile())
                .email(userevent.getEmail())
                .Currency(userevent.getCurrency())
                .accountHolderName(userevent.getAccountHolderName())
                .build();

        assetManagementRepository.save(asset);
        log.info("Initialized asset for user ID: {}", userevent.getUserId()+" for user : {} "+ userevent.getAccountHolderName());
    }

    /**
     * Triggered when a user account is deleted.
     * Deletes all assets linked to that account.
     */
    @KafkaListener(topics = "user.deleted", groupId = "asset-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserDeleted(String event) throws JsonProcessingException {
        log.info("Received user.deleted event for: {}", event);

        ObjectMapper mapper = new ObjectMapper();
        UserEvent userevent = mapper.readValue(event, UserEvent.class);

        List<Asset> assets = assetManagementRepository.findAll()
                .stream()
                .filter(asset -> asset.getUserId().equals(userevent.getUserId()))
                .toList();

        assetManagementRepository.deleteAll(assets);
        log.info("Deleted assets for user ID: {}", userevent.getUserId());
    }

    @KafkaListener(topics = "user.transaction", groupId = "asset-group")
    public void handleTransactionEvent(String eventString) throws JsonProcessingException {
        log.info("Processing transaction event: {}", eventString);

        ObjectMapper mapper = new ObjectMapper();
        TransactionEvent event = mapper.readValue(eventString, TransactionEvent.class);

        // 1. Debit sender's wallet
        processWallet(event.getSenderMob(), event.getAmt() , "DEBIT");

        // 2. Credit receiver's wallet
        processWallet(event.getReceiverMob(), event.getAmt(), "CREDIT");

    }


   // UTILITY function to DEBIT or CREDIT balance
    @Transactional
    public void processWallet(String mobile, int amount, String type) {
        Asset asset = assetManagementRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        switch (type.toUpperCase()) {
            case "DEBIT":
                asset.setValue(asset.getValue() - amount);
                break;
            case "CREDIT":
                asset.setValue(asset.getValue() + amount);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + type);
        }

        assetManagementRepository.save(asset);
    }





}
