package com.hospital.inventory.stock.service;

import java.util.UUID;

import com.hospital.inventory.stock.dto.CreateStockCountRequest;
import com.hospital.inventory.stock.dto.StockCountResponse;

public interface StockCountService {

	StockCountResponse create(CreateStockCountRequest request);

	StockCountResponse close(UUID countId);
}
