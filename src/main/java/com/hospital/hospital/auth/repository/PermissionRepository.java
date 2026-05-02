package com.hospital.hospital.auth.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hospital.hospital.auth.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

	@Query("""
			select distinct permission.code
			from RolePermission rolePermission
			join rolePermission.permission permission
			join rolePermission.role role
			where role.code in :roleCodes
			order by permission.code
			""")
	List<String> findPermissionCodesByRoleCodes(Collection<String> roleCodes);
}
