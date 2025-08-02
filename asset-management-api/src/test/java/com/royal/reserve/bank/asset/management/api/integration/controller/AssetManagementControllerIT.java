package com.royal.reserve.bank.asset.management.api.integration.controller;

import com.royal.reserve.bank.asset.management.api.controller.AssetManagementController;
import com.royal.reserve.bank.asset.management.api.dto.AssetManagementResponse;
import com.royal.reserve.bank.asset.management.api.model.Asset;
import com.royal.reserve.bank.asset.management.api.repository.AssetManagementRepository;
import com.royal.reserve.bank.asset.management.api.service.AssetManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AssetManagementController} class.
 */

@WebMvcTest(controllers = AssetManagementController.class)
class AssetManagementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetManagementService assetManagementService;

    @MockBean
    private AssetManagementRepository assetManagementRepository;

    @Test
    void testIsAssetAvailableReturnsAsset() throws Exception {
        // Given
        Asset dummyAsset = Asset.builder()
                .id(1L)
                .assetCode("GOLD")
                .assetName("Gold Savings")
                .value(5000)
                .mobile("1234567890")
                .build();

        when(assetManagementService.isAssetAvailable("1234567890"))
                .thenReturn(Optional.of(dummyAsset));

        // When & Then
        mockMvc.perform(get("/api/asset-management")
                        .param("mobile", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetCode").value("GOLD"))
                .andExpect(jsonPath("$.assetName").value("Gold Savings"))
                .andExpect(jsonPath("$.value").value(5000))
                .andExpect(jsonPath("$.mobile").value("1234567890"));
    }

    @Test
    void testIsAssetAvailableReturnsEmpty() throws Exception {
        // Given
        String mobile = "9999999999";
        when(assetManagementService.isAssetAvailable(mobile)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/asset-management")
                        .param("mobile", mobile)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("null"));
        // Expect empty string body
    }
}