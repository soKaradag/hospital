package com.hospital.inventory.procurement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.procurement.model.GoodsReceipt;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {

	boolean existsByCodeIgnoreCase(String code);
}
