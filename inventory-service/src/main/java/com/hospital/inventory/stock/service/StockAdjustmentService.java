package com.hospital.inventory.stock.service;

import com.hospital.inventory.stock.dto.CreateStockAdjustmentRequest;
import com.hospital.inventory.stock.dto.StockAdjustmentResponse;

public interface StockAdjustmentService {

	StockAdjustmentResponse create(CreateStockAdjustmentRequest request);
}
