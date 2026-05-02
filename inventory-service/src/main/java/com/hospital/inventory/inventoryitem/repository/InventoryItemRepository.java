package com.hospital.inventory.inventoryitem.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.inventory.inventoryitem.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

	@EntityGraph(attributePaths = { "category", "units" })
	Optional<InventoryItem> findById(UUID id);

	boolean existsByCodeIgnoreCase(String code);

	Optional<InventoryItem> findByCodeIgnoreCase(String code);

	@EntityGraph(attributePaths = { "category", "units" })
	@Query("""
			select distinct item
			from InventoryItem item
			left join item.aliases alias
			left join item.barcodes barcode
			where lower(item.name) like lower(concat('%', :keyword, '%'))
			   or lower(item.code) like lower(concat('%', :keyword, '%'))
			   or lower(alias.alias) like lower(concat('%', :keyword, '%'))
			   or lower(barcode.barcode) like lower(concat('%', :keyword, '%'))
			""")
	Page<InventoryItem> search(@Param("keyword") String keyword, Pageable pageable);

	@EntityGraph(attributePaths = { "category", "units" })
	Page<InventoryItem> findAll(Pageable pageable);
}
