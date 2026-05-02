package com.hospital.hospital.department.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.department.model.DepartmentServiceCatalog;

public interface DepartmentServiceCatalogRepository extends JpaRepository<DepartmentServiceCatalog, UUID> {

	long countByDepartmentId(UUID departmentId);
}
