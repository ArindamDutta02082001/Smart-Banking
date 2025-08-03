package com.royal.reserve.bank.transaction.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.reserve.bank.transaction.api.client.AccountServiceClient;
import com.royal.reserve.bank.transaction.api.client.AssetManagementClient;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.AccountServiceResponse;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.Asset;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.AssetManagementResponse;
import com.royal.reserve.bank.transaction.api.dto.TransactionRequest;
import com.royal.reserve.bank.transaction.api.event.NotifyEvent;
import com.royal.reserve.bank.transaction.api.event.TransactionEvent;
import com.royal.reserve.bank.transaction.api.model.Transaction;
import com.royal.reserve.bank.transaction.api.model.TransactionItems;
import com.royal.reserve.bank.transaction.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class that provides operations for managing transactions.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // Fieng client templates
    private final AssetManagementClient assetManagementClient;
    private final AccountServiceClient accountServiceClient;

    @Autowired
    private final ObjectMapper objectMapper;


    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     *
     *Process a transaction based on the provided transaction request.
     *@param transactionRequest The transaction request containing the necessary information.
     *@return A string message indicating the result of the transaction processing.
     *@throws IllegalArgumentException If any of the requested assets are not available.
     */
    @CacheEvict(value = "assetAvailability", allEntries = true)
    public String processTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {

        // sync get all the users from the user service to check of there exists bth the users
        String receiverNumber = transactionRequest.getReceiverMob();
        String senderNumber = transactionRequest.getSenderMob();

        // Step 1: Fetch all users
        List<AccountServiceResponse> allUsersAccount = accountServiceClient.getAllAccounts();

        System.out.println( "all accnt" + allUsersAccount);

        // Step 2: Extract mobile numbers
        AccountServiceResponse receiverAccount = allUsersAccount.stream()
                .filter(e -> {
                    String mob = e.getAccount().getMobile();
                    return mob != null && mob.equals(receiverNumber);
                })
                .findFirst()
                .orElse(null);

        AccountServiceResponse senderAccount = allUsersAccount.stream()
                .filter(e -> {
                    String mob = e.getAccount().getMobile();
                    return mob != null && mob.equals(senderNumber);
                })
                .findFirst()
                .orElse(null);


        // Step 3: Validate sender and receiver
        if ( receiverAccount == null && senderAccount == null ) return " Receiver & sender dont have a Smart Bank Account";
        else if ( senderAccount == null ) return " Sender dont have a Smart Bank Account";
        else if ( receiverAccount == null ) return " Receiver dont have a Smart Bank Account";

        // validate the sender  assets
        boolean b = checkAssetAvailability(senderNumber , transactionRequest.getAmount().intValue());
        if(!b) return "Insufficient fund for the sender";

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setSenderMob(transactionRequest.getSenderMob());
        transaction.setReceiverMob(transactionRequest.getReceiverMob());
        transaction.setSenderName(senderAccount.getAccount().getAccountHolderName());
        transaction.setReceiverName(receiverAccount.getAccount().getAccountHolderName());

        // 4. Create TransactionItems with amount and purpose
        TransactionItems item = new TransactionItems();
        item.setAmt((int) transactionRequest.getAmount().doubleValue());
        item.setCurrency(transactionRequest.getCurrency());
        item.setMessage(transactionRequest.getPurpose());

        transaction.setTransactionItemsList(List.of(item));

        // 5. Save transaction
        transactionRepository.save(transaction);

        // 6. publish the transaction event to the user.transaction topic to be consumed by the asset servcie
        // creating a transaction event

        TransactionEvent event = new TransactionEvent(
                transaction.getTransactionId(),
                transaction.getSenderName(),
                transaction.getReceiverName(),
                transaction.getSenderMob(),
                transaction.getReceiverMob(),
                transactionRequest.getPurpose(),
                transactionRequest.getCurrency(),
                transactionRequest.getAmount().intValue()
        );

        String json = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("user.transaction", json);


        // 8. Send to notification topic

        NotifyEvent notifyEvent = new NotifyEvent(
                transaction.getTransactionId(),
                transaction.getSenderName(),
                transaction.getReceiverName(),
                transaction.getSenderMob(),
                transaction.getReceiverMob(),
                senderAccount.getAccount().getEmail(),
                receiverAccount.getAccount().getEmail(),
                transactionRequest.getPurpose(),
                transactionRequest.getCurrency(),
                transactionRequest.getAmount().intValue()
        );

        String json1 = objectMapper.writeValueAsString(notifyEvent);
        kafkaTemplate.send("user.notify", json1);

        return "Transaction is processed ";

    }

    /**
     * Checks the availability of assets.
     * @return true if all assets are available, false otherwise.
     */
    @Cacheable("assetAvailability")
    public boolean checkAssetAvailability(String mobile, int amount) {
        Optional<Asset> assetManagementResponse = assetManagementClient.checkAssetAvailability(mobile);
        return assetManagementResponse.get().getValue() >= amount;
    }


    public List<Transaction> getAllTransaction(String senderMobile) {
        return transactionRepository.findBySenderMob(senderMobile);
    }


}
