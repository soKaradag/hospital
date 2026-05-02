package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SurgeryStockReservationResponse {

	private UUID surgeryId;
	private String status;
	private List<ReservationLine> reservations;

	public static class ReservationLine {
		private UUID reservationId;
		private String inventoryItemCode;
		private BigDecimal quantity;
		private String status;

		public UUID getReservationId() {
			return reservationId;
		}

		public void setReservationId(UUID reservationId) {
			this.reservationId = reservationId;
		}

		public String getInventoryItemCode() {
			return inventoryItemCode;
		}

		public void setInventoryItemCode(String inventoryItemCode) {
			this.inventoryItemCode = inventoryItemCode;
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
	}

	public UUID getSurgeryId() {
		return surgeryId;
	}

	public void setSurgeryId(UUID surgeryId) {
		this.surgeryId = surgeryId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ReservationLine> getReservations() {
		return reservations;
	}

	public void setReservations(List<ReservationLine> reservations) {
		this.reservations = reservations;
	}
}
