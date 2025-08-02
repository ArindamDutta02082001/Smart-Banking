package com.royal.reserve.bank.transaction.api.client;

import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.AccountServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

/**
 * A Feign client interface for interacting with the account service API.
 */
@FeignClient(name = "account-api")
//@Retry(name = "asset-management")
public interface AccountServiceClient {

    /**
     *
     * Retrieves asset availability information from the Asset Management API.
     */
    @GetMapping("/api/account")
    List<AccountServiceResponse> getAllAccounts();
}