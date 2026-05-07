package com.hospital.hospital.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hospital.hospital.auth.model.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

	long countByRole_Id(UUID roleId);

	void deleteByRole_Id(UUID roleId);

	@Query("""
			select rolePermission.permission.code
			from RolePermission rolePermission
			where rolePermission.role.id = :roleId
			order by rolePermission.permission.code
			""")
	List<String> findPermissionCodesByRoleId(UUID roleId);
}
