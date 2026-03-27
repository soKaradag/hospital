package com.hospital.hospital.auth.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * Refresh token, kullanıcıların oturumlarını uzatmak için kullanılır.
 * Access token süresi dolduğunda, refresh token kullanılarak yeni bir access token alınır.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

	public RefreshToken() {
	}

	public RefreshToken(User user, String tokenHash, Instant expiresAt) {
		this.user = user;
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// Refresh token veritabanında düz metin yerine hash olarak tutulur.
	@Column(name = "token_hash", nullable = false, length = 255)
	private String tokenHash;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Instant getRevokedAt() {
		return revokedAt;
	}

	public void setRevokedAt(Instant revokedAt) {
		this.revokedAt = revokedAt;
	}
}
