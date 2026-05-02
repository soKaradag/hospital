package com.hospital.hospital.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hospital.hospital.auth.model.RoleEntity;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

	boolean existsByUserAndRole(User user, RoleEntity role);

	@Query("""
			select userRole.role.code
			from UserRole userRole
			where userRole.user.id = :userId
			order by userRole.primaryRole desc, userRole.role.code asc
			""")
	List<String> findRoleCodesByUserId(UUID userId);

	@Query("""
			select userRole.role.code
			from UserRole userRole
			where userRole.user.id = :userId and userRole.primaryRole = true
			order by userRole.role.code asc
			""")
	Optional<String> findPrimaryRoleCodeByUserId(UUID userId);
}
