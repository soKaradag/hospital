package com.hospital.hospital.surgery.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.OperatingRoom;

public interface OperatingRoomRepository extends JpaRepository<OperatingRoom, UUID> {

	boolean existsByCodeIgnoreCase(String code);
}
