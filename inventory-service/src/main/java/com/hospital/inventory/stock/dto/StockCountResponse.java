package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class StockCountResponse {

	private UUID id;
	private UUID warehouseId;
	private UUID warehouseZoneId;
	private String status;
	private String notes;
	private Instant closedAt;
	private Instant createdAt;
	private Instant updatedAt;
	private List<Line> lines;

	public static class Line {
		private UUID id;
		private UUID itemId;
		private UUID batchId;
		private BigDecimal expectedQuantity;
		private BigDecimal countedQuantity;
		private BigDecimal differenceQuantity;
		private String notes;

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public UUID getItemId() {
			return itemId;
		}

		public void setItemId(UUID itemId) {
			this.itemId = itemId;
		}

		public UUID getBatchId() {
			return batchId;
		}

		public void setBatchId(UUID batchId) {
			this.batchId = batchId;
		}

		public BigDecimal getExpectedQuantity() {
			return expectedQuantity;
		}

		public void setExpectedQuantity(BigDecimal expectedQuantity) {
			this.expectedQuantity = expectedQuantity;
		}

		public BigDecimal getCountedQuantity() {
			return countedQuantity;
		}

		public void setCountedQuantity(BigDecimal countedQuantity) {
			this.countedQuantity = countedQuantity;
		}

		public BigDecimal getDifferenceQuantity() {
			return differenceQuantity;
		}

		public void setDifferenceQuantity(BigDecimal differenceQuantity) {
			this.differenceQuantity = differenceQuantity;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(UUID warehouseId) {
		this.warehouseId = warehouseId;
	}

	public UUID getWarehouseZoneId() {
		return warehouseZoneId;
	}

	public void setWarehouseZoneId(UUID warehouseZoneId) {
		this.warehouseZoneId = warehouseZoneId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Instant getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(Instant closedAt) {
		this.closedAt = closedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
}
