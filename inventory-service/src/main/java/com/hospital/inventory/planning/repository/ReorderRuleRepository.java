package com.hospital.inventory.planning.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.inventory.planning.model.ReorderRule;

public interface ReorderRuleRepository extends JpaRepository<ReorderRule, UUID> {

	@EntityGraph(attributePaths = { "inventoryItem", "warehouse", "warehouseZone", "preferredSupplier" })
	Page<ReorderRule> findAll(Pageable pageable);

	@EntityGraph(attributePaths = { "inventoryItem", "warehouse", "warehouseZone", "preferredSupplier" })
	Page<ReorderRule> findAllByActiveTrue(Pageable pageable);

	@EntityGraph(attributePaths = { "inventoryItem", "warehouse", "warehouseZone", "preferredSupplier" })
	java.util.List<ReorderRule> findAllByActiveTrue();

	@EntityGraph(attributePaths = { "inventoryItem", "warehouse", "warehouseZone", "preferredSupplier" })
	java.util.Optional<ReorderRule> findByIdAndActiveTrue(UUID id);

	boolean existsByInventoryItemIdAndActiveTrue(UUID inventoryItemId);

	@Query("""
			select count(rule) > 0
			from ReorderRule rule
			where rule.inventoryItem.id = :itemId
			  and rule.warehouse.id = :warehouseId
			  and ((:warehouseZoneId is null and rule.warehouseZone is null)
			    or rule.warehouseZone.id = :warehouseZoneId)
			""")
	boolean existsForLocation(
			@Param("itemId") UUID itemId,
			@Param("warehouseId") UUID warehouseId,
			@Param("warehouseZoneId") UUID warehouseZoneId);
}
