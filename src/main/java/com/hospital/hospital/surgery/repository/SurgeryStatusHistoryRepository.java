package com.hospital.hospital.surgery.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.SurgeryStatusHistory;

public interface SurgeryStatusHistoryRepository extends JpaRepository<SurgeryStatusHistory, UUID> {

	long countBySurgeryId(UUID surgeryId);
}
