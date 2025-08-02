package com.royal.reserve.bank.asset.management.api.unit.controller;

import com.royal.reserve.bank.asset.management.api.controller.AssetManagementController;
import com.royal.reserve.bank.asset.management.api.dto.AssetManagementResponse;
import com.royal.reserve.bank.asset.management.api.model.Asset;
import com.royal.reserve.bank.asset.management.api.repository.AssetManagementRepository;
import com.royal.reserve.bank.asset.management.api.service.AssetManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AssetManagementController} class.
 */
@ExtendWith(MockitoExtension.class)
class AssetManagementControllerTest {

    @Mock
    private AssetManagementService assetManagementService;

    @Mock
    private AssetManagementRepository assetManagementRepository;

    /**
     */
    @Test
    void testIsAssetAvailableReturnsOk() {
        // Given
        AssetManagementController assetManagementController = new AssetManagementController(assetManagementService , assetManagementRepository);
        List<String> assetCodes = Arrays.asList("8917", "1355");


        Asset dummyAsset = Asset.builder()
                .assetCode("8917")
                .assetName("Gold")
                .value(1000)
                .build();

        AssetManagementResponse assetManagementResponse = new AssetManagementResponse("8917",dummyAsset, true);
        when(assetManagementService.isAssetAvailable(String.valueOf(8917))).thenReturn(Optional.ofNullable(dummyAsset));

        // When
        ResponseEntity<Optional<Asset>> response =
                ResponseEntity.ok(assetManagementController.isAssetAvailable(String.valueOf(8917)));

        // Then
        verify(assetManagementService, times(1)).isAssetAvailable(String.valueOf(8917));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dummyAsset, response.getBody());
    }

    /**
     *
     */
    @Test
    void testIsAssetAvailableWithEmptyAssetCodeList() {
        // Given
        AssetManagementController assetManagementController = new AssetManagementController(assetManagementService , assetManagementRepository);
        List<String> emptyAssetCodes = List.of();

        // When
        Optional<Asset> response = assetManagementController.isAssetAvailable("");

        // Then
        assertTrue(response.isEmpty());
    }
}
