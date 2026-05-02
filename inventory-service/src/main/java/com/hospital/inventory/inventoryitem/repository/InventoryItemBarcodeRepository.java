package com.hospital.inventory.inventoryitem.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.inventoryitem.model.InventoryItemBarcode;

public interface InventoryItemBarcodeRepository extends JpaRepository<InventoryItemBarcode, UUID> {

	boolean existsByBarcodeIgnoreCase(String barcode);

	Optional<InventoryItemBarcode> findByBarcodeIgnoreCase(String barcode);
}
