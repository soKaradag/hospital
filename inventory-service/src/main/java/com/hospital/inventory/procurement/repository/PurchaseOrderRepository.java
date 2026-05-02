package com.hospital.inventory.procurement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.procurement.model.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

	boolean existsByCodeIgnoreCase(String code);

	@EntityGraph(attributePaths = { "supplier", "items", "items.inventoryItem", "items.supplierCatalogItem" })
	Optional<PurchaseOrder> findById(UUID id);

	@EntityGraph(attributePaths = { "supplier", "items" })
	Page<PurchaseOrder> findAll(Pageable pageable);
}
