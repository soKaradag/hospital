package com.hospital.inventory.stock.model;

import java.math.BigDecimal;

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
@Table(name = "stock_transfer_requests")
public class StockTransferRequest extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_batch_id", nullable = false)
	private StockBatch stockBatch;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "from_warehouse_id", nullable = false)
	private Warehouse fromWarehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_warehouse_zone_id")
	private WarehouseZone fromWarehouseZone;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "to_warehouse_id", nullable = false)
	private Warehouse toWarehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_warehouse_zone_id")
	private WarehouseZone toWarehouseZone;

	@Column(name = "quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal quantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 40)
	private StockTransferRequestStatus status;

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

	public Warehouse getFromWarehouse() {
		return fromWarehouse;
	}

	public void setFromWarehouse(Warehouse fromWarehouse) {
		this.fromWarehouse = fromWarehouse;
	}

	public WarehouseZone getFromWarehouseZone() {
		return fromWarehouseZone;
	}

	public void setFromWarehouseZone(WarehouseZone fromWarehouseZone) {
		this.fromWarehouseZone = fromWarehouseZone;
	}

	public Warehouse getToWarehouse() {
		return toWarehouse;
	}

	public void setToWarehouse(Warehouse toWarehouse) {
		this.toWarehouse = toWarehouse;
	}

	public WarehouseZone getToWarehouseZone() {
		return toWarehouseZone;
	}

	public void setToWarehouseZone(WarehouseZone toWarehouseZone) {
		this.toWarehouseZone = toWarehouseZone;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public StockTransferRequestStatus getStatus() {
		return status;
	}

	public void setStatus(StockTransferRequestStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
