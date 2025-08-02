package com.royal.reserve.bank.asset.management.api.dto;

import com.royal.reserve.bank.asset.management.api.model.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class that represents the response payload for an asset.
 * <p>
 * The class needs to be serializable to allow caching.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetManagementResponse implements Serializable {
    private String mobile ;
    private Asset asset;
    private boolean isAssetAvailable;
}
