package com.hospital.inventory.stock.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.stock.model.StockAdjustment;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, UUID> {
}
