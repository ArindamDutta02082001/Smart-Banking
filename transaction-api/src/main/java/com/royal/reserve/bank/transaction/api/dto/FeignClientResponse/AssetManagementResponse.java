package com.royal.reserve.bank.transaction.api.dto.FeignClientResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) class that represents the response payload for an asset.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetManagementResponse {
    private String mobile ;
    private Asset asset;
    private boolean isAssetAvailable;
}
