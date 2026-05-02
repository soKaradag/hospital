package com.hospital.inventory.stock.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.hospital.inventory.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_transfers")
public class StockTransfer extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "transfer_request_id", nullable = false)
	private StockTransferRequest transferRequest;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "source_batch_id", nullable = false)
	private StockBatch sourceBatch;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "destination_batch_id", nullable = false)
	private StockBatch destinationBatch;

	@Column(name = "quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal quantity;

	@Column(name = "completed_at", nullable = false)
	private Instant completedAt;

	public StockTransferRequest getTransferRequest() {
		return transferRequest;
	}

	public void setTransferRequest(StockTransferRequest transferRequest) {
		this.transferRequest = transferRequest;
	}

	public StockBatch getSourceBatch() {
		return sourceBatch;
	}

	public void setSourceBatch(StockBatch sourceBatch) {
		this.sourceBatch = sourceBatch;
	}

	public StockBatch getDestinationBatch() {
		return destinationBatch;
	}

	public void setDestinationBatch(StockBatch destinationBatch) {
		this.destinationBatch = destinationBatch;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}
}
