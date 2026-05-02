package com.hospital.inventory.stock.model;

import java.math.BigDecimal;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_adjustments")
public class StockAdjustment extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_batch_id", nullable = false)
	private StockBatch stockBatch;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_zone_id")
	private WarehouseZone warehouseZone;

	@Column(name = "quantity_delta", nullable = false, precision = 19, scale = 4)
	private BigDecimal quantityDelta;

	@Column(name = "reason_code", nullable = false, length = 80)
	private String reasonCode;

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

	public BigDecimal getQuantityDelta() {
		return quantityDelta;
	}

	public void setQuantityDelta(BigDecimal quantityDelta) {
		this.quantityDelta = quantityDelta;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
