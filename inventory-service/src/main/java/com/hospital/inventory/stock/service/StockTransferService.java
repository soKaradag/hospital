package com.hospital.inventory.stock.service;

import com.hospital.inventory.stock.dto.CreateStockTransferRequest;
import com.hospital.inventory.stock.dto.StockTransferResponse;

public interface StockTransferService {

	StockTransferResponse create(CreateStockTransferRequest request);
}
