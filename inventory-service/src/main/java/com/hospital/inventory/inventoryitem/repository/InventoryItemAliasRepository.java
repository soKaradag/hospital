package com.hospital.inventory.inventoryitem.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.inventoryitem.model.InventoryItemAlias;

public interface InventoryItemAliasRepository extends JpaRepository<InventoryItemAlias, UUID> {

	boolean existsByAliasIgnoreCase(String alias);

	Optional<InventoryItemAlias> findByAliasIgnoreCase(String alias);
}
