package com.royal.reserve.bank.asset.management.api.unit.repository;
import com.royal.reserve.bank.asset.management.api.model.Asset;
import com.royal.reserve.bank.asset.management.api.repository.AssetManagementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/*
Unit tests for the {@link AssetManagementRepository} class.
 */
@ExtendWith(MockitoExtension.class)
class AssetManagementRepositoryTest {

    @Mock
    private AssetManagementRepository assetManagementRepository;

    /**
     * Test for the {@link AssetManagementRepository#findByMobile(String)} method.
     */
    @Test
    void testFindByMobile() {
        // Given
        String mobile = "9999999999";
        Asset asset = Asset.builder()
                .id(1L)
                .UserId("12345")
                .assetCode("78231")
                .assetName("a")
                .mobile(mobile)
                .value(25400)
                .build();

        when(assetManagementRepository.findByMobile(mobile)).thenReturn(Optional.of(asset));

        // When
        Optional<Asset> actualAsset = assetManagementRepository.findByMobile(mobile);

        // Then
        assertEquals(Optional.of(asset), actualAsset);
    }
}