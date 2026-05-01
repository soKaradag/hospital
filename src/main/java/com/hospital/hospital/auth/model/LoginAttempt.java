package com.hospital.hospital.auth.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "login_attempts")
public class LoginAttempt extends BaseEntity {

	public LoginAttempt() {
	}

	public LoginAttempt(
			User user,
			String username,
			boolean success,
			String failureReason,
			String ipAddress,
			String userAgent,
			Instant attemptedAt) {
		this.user = user;
		this.username = username;
		this.success = success;
		this.failureReason = failureReason;
		this.ipAddress = ipAddress;
		this.userAgent = userAgent;
		this.attemptedAt = attemptedAt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "username", nullable = false, length = 100)
	private String username;

	@Column(name = "success", nullable = false)
	private boolean success;

	@Column(name = "failure_reason", length = 255)
	private String failureReason;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "user_agent", length = 255)
	private String userAgent;

	@Column(name = "attempted_at", nullable = false)
	private Instant attemptedAt;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Instant getAttemptedAt() {
		return attemptedAt;
	}

	public void setAttemptedAt(Instant attemptedAt) {
		this.attemptedAt = attemptedAt;
	}
}
