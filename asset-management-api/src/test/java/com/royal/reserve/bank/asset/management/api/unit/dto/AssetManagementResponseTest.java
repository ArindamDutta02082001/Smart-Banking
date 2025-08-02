package com.royal.reserve.bank.asset.management.api.unit.dto;

import com.royal.reserve.bank.asset.management.api.dto.AssetManagementResponse;
import com.royal.reserve.bank.asset.management.api.model.Asset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AssetManagementResponse} class.
 */
class AssetManagementResponseTest {

    /**
     * Test the constructors.
     */
    @Test
    void testAssetManagementResponse() {
        // Given
        String expectedAssetCode = "USDT";
        boolean expectedIsAssetAvailable = true;

        Asset asset = Asset.builder()
                .assetCode(expectedAssetCode)
                .assetName("Tether")
                .value(1000)
                .build();

        // When
        AssetManagementResponse response = AssetManagementResponse.builder()
                .mobile("1234567890")
                .asset(asset)
                .isAssetAvailable(expectedIsAssetAvailable)
                .build();

        // Then
        Assertions.assertEquals(expectedAssetCode, response.getAsset().getAssetCode());
        Assertions.assertEquals("Tether", response.getAsset().getAssetName());
        Assertions.assertEquals(1000, response.getAsset().getValue());
        Assertions.assertEquals("1234567890", response.getMobile());
        Assertions.assertTrue(response.isAssetAvailable());
    }
}
