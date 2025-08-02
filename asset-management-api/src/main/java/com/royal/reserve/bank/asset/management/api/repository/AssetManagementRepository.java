package com.royal.reserve.bank.asset.management.api.repository;

import com.royal.reserve.bank.asset.management.api.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for storing assets in the database.
 * It provides CRUD operations and other database-related functionality.
 */
public interface AssetManagementRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByMobile(String mobile);
}
