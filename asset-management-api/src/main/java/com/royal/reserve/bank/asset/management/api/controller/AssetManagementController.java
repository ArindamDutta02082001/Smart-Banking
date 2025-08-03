package com.royal.reserve.bank.asset.management.api.controller;

import com.royal.reserve.bank.asset.management.api.model.Asset;
import com.royal.reserve.bank.asset.management.api.repository.AssetManagementRepository;
import com.royal.reserve.bank.asset.management.api.service.AssetManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller class that handles HTTP requests related to asset management.
 */
@RestController
@RequestMapping("/api/asset-management")
@RequiredArgsConstructor
@Slf4j
public class AssetManagementController {

    private final AssetManagementService assetManagementService;

    private final AssetManagementRepository assetRepository;

    /**
     * Retrieves the availability status of assets based on their codes.
     * @return The list of asset management responses containing the availability status for each asset code.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Optional<Asset> isAssetAvailable(@RequestParam String mobile) {
        log.info("Received asset availability check request for mobile no : {}", mobile);
        return assetManagementService.isAssetAvailable(mobile);
    }

    // not ideal but ust for test purpose we will check all the assets created

    /**
     * Fetch all assets from the database.
     * @return List of all assets in the system.
     */
    @GetMapping("/all-assets")
    public ResponseEntity<List<Asset>> getAllAssets() {
        List<Asset> allAssets = assetRepository.findAll();
        return ResponseEntity.ok(allAssets);
    }


}

