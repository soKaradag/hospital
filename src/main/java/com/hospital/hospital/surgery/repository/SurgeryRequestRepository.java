package com.hospital.hospital.surgery.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.SurgeryRequest;

public interface SurgeryRequestRepository extends JpaRepository<SurgeryRequest, UUID> {
}
