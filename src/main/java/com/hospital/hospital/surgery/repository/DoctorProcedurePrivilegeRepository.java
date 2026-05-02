package com.hospital.hospital.surgery.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.DoctorProcedurePrivilege;

public interface DoctorProcedurePrivilegeRepository extends JpaRepository<DoctorProcedurePrivilege, UUID> {

	boolean existsByDoctorIdAndProcedureCodeIgnoreCase(UUID doctorId, String procedureCode);

	Optional<DoctorProcedurePrivilege> findByDoctorIdAndProcedureCodeIgnoreCase(UUID doctorId, String procedureCode);
}
