package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class StockAvailabilityResponse {

	private UUID itemId;
	private BigDecimal totalOnHand;
	private BigDecimal reservedQuantity;
	private BigDecimal availableQuantity;
	private List<StockAvailabilityBatchResponse> batches;

	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getTotalOnHand() {
		return totalOnHand;
	}

	public void setTotalOnHand(BigDecimal totalOnHand) {
		this.totalOnHand = totalOnHand;
	}

	public BigDecimal getReservedQuantity() {
		return reservedQuantity;
	}

	public void setReservedQuantity(BigDecimal reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}

	public BigDecimal getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(BigDecimal availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public List<StockAvailabilityBatchResponse> getBatches() {
		return batches;
	}

	public void setBatches(List<StockAvailabilityBatchResponse> batches) {
		this.batches = batches;
	}
}
