package com.royal.reserve.bank.asset.management.api.unit.service;

import com.royal.reserve.bank.asset.management.api.dto.AssetManagementResponse;
import com.royal.reserve.bank.asset.management.api.model.Asset;
import com.royal.reserve.bank.asset.management.api.repository.AssetManagementRepository;
import com.royal.reserve.bank.asset.management.api.service.AssetManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link AssetManagementService} class.
 */
@ExtendWith(MockitoExtension.class)
class AssetManagementServiceTest {

    @Mock
    private AssetManagementRepository assetManagementRepository;

    @InjectMocks
    private AssetManagementService assetManagementService;

    @Test
    void testIsAssetAvailableReturnsAsset() {
        // Given
        String mobile = "9999999999";

        Asset asset = Asset.builder()
                .id(1L)
                .UserId("user123")
                .assetCode("ASSET001")
                .assetName("Digital Coin")
                .value(5000)
                .mobile(mobile)
                .build();

        when(assetManagementRepository.findByMobile(mobile)).thenReturn(Optional.of(asset));

        // When
        Optional<Asset> result = assetManagementService.isAssetAvailable(mobile);

        // Then
        assertTrue(result.isPresent());
        assertEquals("ASSET001", result.get().getAssetCode());
        assertEquals(5000, result.get().getValue());
        assertEquals("Digital Coin", result.get().getAssetName());
    }

    @Test
    void testIsAssetAvailableReturnsEmpty() {
        // Given
        String mobile = "unknown-number";

        when(assetManagementRepository.findByMobile(mobile)).thenReturn(Optional.empty());

        // When
        Optional<Asset> result = assetManagementService.isAssetAvailable(mobile);

        // Then
        assertTrue(result.isEmpty());
    }
}
