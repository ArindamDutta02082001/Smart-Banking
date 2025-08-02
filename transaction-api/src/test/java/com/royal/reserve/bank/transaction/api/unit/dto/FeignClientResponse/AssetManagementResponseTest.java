package com.royal.reserve.bank.transaction.api.unit.dto.FeignClientResponse;

import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.Asset;
import com.royal.reserve.bank.transaction.api.dto.FeignClientResponse.AssetManagementResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AssetManagementResponse} class.
 */
class AssetManagementResponseTest {

    /**
     * Test the builder and equals logic for AssetManagementResponse.
     */
    @Test
    void testAssetManagementResponseBuilderAndGetters() {
        // Given
        String expectedMobile = "9999999999";
        Asset expectedAsset = Asset.builder()
                .assetCode("F")
                .assetName("Ford Motor Company")
                .value(5000)
                .UserId("user123")
                .mobile("9999999999")
                .email("ford@example.com")
                .accountHolderName("Henry Ford")
                .build();
        boolean expectedIsAssetAvailable = true;

        // When
        AssetManagementResponse response = AssetManagementResponse.builder()
                .mobile(expectedMobile)
                .asset(expectedAsset)
                .isAssetAvailable(expectedIsAssetAvailable)
                .build();

        // Then
        Assertions.assertEquals(expectedMobile, response.getMobile());
        Assertions.assertEquals(expectedAsset, response.getAsset());  // Uses overridden equals
        Assertions.assertTrue(response.isAssetAvailable());
    }
}
