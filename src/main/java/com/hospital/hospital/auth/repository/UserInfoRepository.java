package com.hospital.hospital.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.auth.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

	Optional<UserInfo> findByUser_Id(UUID userId);

	boolean existsByUser_Id(UUID userId);
}
