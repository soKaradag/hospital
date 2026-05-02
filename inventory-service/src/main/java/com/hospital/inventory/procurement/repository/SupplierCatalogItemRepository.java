package com.hospital.inventory.procurement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.procurement.model.SupplierCatalogItem;

public interface SupplierCatalogItemRepository extends JpaRepository<SupplierCatalogItem, UUID> {

	boolean existsBySupplierIdAndInventoryItemIdAndUnitCodeIgnoreCase(UUID supplierId, UUID inventoryItemId, String unitCode);

	List<SupplierCatalogItem> findAllBySupplierId(UUID supplierId);
}
