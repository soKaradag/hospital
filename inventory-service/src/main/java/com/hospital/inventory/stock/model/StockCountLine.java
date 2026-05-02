package com.hospital.inventory.stock.model;

import java.math.BigDecimal;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.inventoryitem.model.InventoryItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_count_lines")
public class StockCountLine extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_count_id", nullable = false)
	private StockCount stockCount;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_batch_id", nullable = false)
	private StockBatch stockBatch;

	@Column(name = "expected_quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal expectedQuantity;

	@Column(name = "counted_quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal countedQuantity;

	@Column(name = "difference_quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal differenceQuantity;

	@Column(name = "notes", length = 255)
	private String notes;

	public StockCount getStockCount() {
		return stockCount;
	}

	public void setStockCount(StockCount stockCount) {
		this.stockCount = stockCount;
	}

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
