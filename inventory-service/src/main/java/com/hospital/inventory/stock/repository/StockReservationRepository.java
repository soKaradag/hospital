package com.hospital.inventory.stock.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockReservation;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

	@Query("""
			select coalesce(sum(reservation.quantity), 0)
			from StockReservation reservation
			where reservation.inventoryItem.id = :itemId
			  and reservation.status = :status
			""")
	BigDecimal sumQuantityByItemIdAndStatus(@Param("itemId") UUID itemId, @Param("status") ReservationStatus status);

	@Query("""
			select coalesce(sum(reservation.quantity), 0)
			from StockReservation reservation
			where reservation.stockBatch.id = :batchId
			  and reservation.status = :status
			""")
	BigDecimal sumQuantityByBatchIdAndStatus(@Param("batchId") UUID batchId, @Param("status") ReservationStatus status);

	Optional<StockReservation> findByIdAndStatus(UUID id, ReservationStatus status);
}
