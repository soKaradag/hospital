package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class StockTransferResponse {

	private UUID requestId;
	private UUID transferId;
	private UUID itemId;
	private UUID sourceBatchId;
	private UUID destinationBatchId;
	private UUID fromWarehouseId;
	private UUID fromWarehouseZoneId;
	private UUID toWarehouseId;
	private UUID toWarehouseZoneId;
	private BigDecimal quantity;
	private String status;
	private Instant completedAt;

	public UUID getRequestId() {
		return requestId;
	}

	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

	public UUID getTransferId() {
		return transferId;
	}

	public void setTransferId(UUID transferId) {
		this.transferId = transferId;
	}

	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}

	public UUID getSourceBatchId() {
		return sourceBatchId;
	}

	public void setSourceBatchId(UUID sourceBatchId) {
		this.sourceBatchId = sourceBatchId;
	}

	public UUID getDestinationBatchId() {
		return destinationBatchId;
	}

	public void setDestinationBatchId(UUID destinationBatchId) {
		this.destinationBatchId = destinationBatchId;
	}

	public UUID getFromWarehouseId() {
		return fromWarehouseId;
	}

	public void setFromWarehouseId(UUID fromWarehouseId) {
		this.fromWarehouseId = fromWarehouseId;
	}

	public UUID getFromWarehouseZoneId() {
		return fromWarehouseZoneId;
	}

	public void setFromWarehouseZoneId(UUID fromWarehouseZoneId) {
		this.fromWarehouseZoneId = fromWarehouseZoneId;
	}

	public UUID getToWarehouseId() {
		return toWarehouseId;
	}

	public void setToWarehouseId(UUID toWarehouseId) {
		this.toWarehouseId = toWarehouseId;
	}

	public UUID getToWarehouseZoneId() {
		return toWarehouseZoneId;
	}

	public void setToWarehouseZoneId(UUID toWarehouseZoneId) {
		this.toWarehouseZoneId = toWarehouseZoneId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}
}
