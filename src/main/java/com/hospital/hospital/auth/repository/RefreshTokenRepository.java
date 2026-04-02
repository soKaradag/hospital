package com.hospital.hospital.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.auth.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	//Optional çünkü token hash'i bulunamayabilir.
	Optional<RefreshToken> findByTokenHash(String tokenHash);

}
