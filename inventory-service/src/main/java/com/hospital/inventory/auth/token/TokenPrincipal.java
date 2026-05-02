package com.hospital.inventory.auth.token;

import java.time.Instant;
import java.util.UUID;

public record TokenPrincipal(
		UUID userId,
		String username,
		String role,
		TokenType tokenType,
		Instant issuedAt,
		Instant expiresAt) {
}
