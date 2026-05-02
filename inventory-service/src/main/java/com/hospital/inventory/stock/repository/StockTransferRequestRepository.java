package com.hospital.inventory.stock.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.stock.model.StockTransferRequest;

public interface StockTransferRequestRepository extends JpaRepository<StockTransferRequest, UUID> {
}
