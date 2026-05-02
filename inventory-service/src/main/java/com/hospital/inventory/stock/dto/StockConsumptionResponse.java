package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class StockConsumptionResponse {

	private String inventoryItemCode;
	private String warehouseCode;
	private String warehouseZoneCode;
	private BigDecimal requestedQuantity;
	private BigDecimal consumedQuantity;
	private List<Line> lines;

	public static class Line {
		private UUID stockBatchId;
		private String batchNumber;
		private BigDecimal quantity;

		public UUID getStockBatchId() {
			return stockBatchId;
		}

		public void setStockBatchId(UUID stockBatchId) {
			this.stockBatchId = stockBatchId;
		}

		public String getBatchNumber() {
			return batchNumber;
		}

		public void setBatchNumber(String batchNumber) {
			this.batchNumber = batchNumber;
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public void setQuantity(BigDecimal quantity) {
			this.quantity = quantity;
		}
	}

	public String getInventoryItemCode() {
		return inventoryItemCode;
	}

	public void setInventoryItemCode(String inventoryItemCode) {
		this.inventoryItemCode = inventoryItemCode;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getWarehouseZoneCode() {
		return warehouseZoneCode;
	}

	public void setWarehouseZoneCode(String warehouseZoneCode) {
		this.warehouseZoneCode = warehouseZoneCode;
	}

	public BigDecimal getRequestedQuantity() {
		return requestedQuantity;
	}

	public void setRequestedQuantity(BigDecimal requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public BigDecimal getConsumedQuantity() {
		return consumedQuantity;
	}

	public void setConsumedQuantity(BigDecimal consumedQuantity) {
		this.consumedQuantity = consumedQuantity;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
}
