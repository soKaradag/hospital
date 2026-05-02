package com.hospital.inventory.stock.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_reservations")
public class StockReservation extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stock_batch_id")
	private StockBatch stockBatch;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_zone_id")
	private WarehouseZone warehouseZone;

	@Column(name = "quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal quantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 40)
	private ReservationStatus status;

	@Column(name = "reservation_type", nullable = false, length = 80)
	private String reservationType;

	@Column(name = "reference_type", length = 80)
	private String referenceType;

	@Column(name = "reference_id", length = 80)
	private String referenceId;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "notes", length = 255)
	private String notes;

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public StockBatch getStockBatch() {
		return stockBatch;
	}

	public void setStockBatch(StockBatch stockBatch) {
		this.stockBatch = stockBatch;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public WarehouseZone getWarehouseZone() {
		return warehouseZone;
	}

	public void setWarehouseZone(WarehouseZone warehouseZone) {
		this.warehouseZone = warehouseZone;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public String getReservationType() {
		return reservationType;
	}

	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
