package com.hospital.hospital.auth.model;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

	public User() {
	}

	public User(String username, String passwordHash, Role role) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.role = role;
		this.status = UserStatus.ACTIVE;
	}

	// Username giriş kimliğidir ve sistem genelinde benzersiz olmalıdır.
	@Column(name = "username", nullable = false, unique = true, length = 100)
	private String username;

	// Şifre hiçbir zaman düz metin tutulmaz; sadece hash değeri saklanır.
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private UserStatus status = UserStatus.ACTIVE;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 30)
	private Role role;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserInfo userInfo;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
