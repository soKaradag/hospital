package com.hospital.inventory.stock.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.stock.model.StockCountLine;

public interface StockCountLineRepository extends JpaRepository<StockCountLine, UUID> {

	@EntityGraph(attributePaths = { "inventoryItem", "stockBatch" })
	List<StockCountLine> findAllByStockCountId(UUID stockCountId);
}
