package com.hospital.hospital.surgery.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.SurgeryTeamAssignment;

public interface SurgeryTeamAssignmentRepository extends JpaRepository<SurgeryTeamAssignment, UUID> {

	long countBySurgeryId(UUID surgeryId);
}
