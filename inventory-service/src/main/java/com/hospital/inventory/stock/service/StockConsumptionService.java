package com.hospital.inventory.stock.service;

import com.hospital.inventory.stock.dto.CreateStockConsumptionRequest;
import com.hospital.inventory.stock.dto.StockConsumptionResponse;

public interface StockConsumptionService {

	StockConsumptionResponse consume(CreateStockConsumptionRequest request);
}
