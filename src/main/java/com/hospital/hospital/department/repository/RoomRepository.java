package com.hospital.hospital.department.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.department.model.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

	long countByDepartmentId(UUID departmentId);
}
