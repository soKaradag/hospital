package com.hospital.hospital.auth.token;

import java.time.Instant;
import java.util.UUID;

import com.hospital.hospital.auth.model.Role;

/*
 * JWT token'ın payload kısmından çözülen verileri tutan compact data class.
 */
public record TokenPrincipal(
		UUID userId,
		String username,
		Role role,
		TokenType tokenType,
		Instant issuedAt,
		Instant expiresAt) {
}
