package com.hospital.hospital.auth.model;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles")
public class UserRole extends BaseEntity {

	public UserRole() {
	}

	public UserRole(User user, RoleEntity role, boolean primaryRole) {
		this.user = user;
		this.role = role;
		this.primaryRole = primaryRole;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private RoleEntity role;

	@Column(name = "is_primary", nullable = false)
	private boolean primaryRole;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

	public boolean isPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(boolean primaryRole) {
		this.primaryRole = primaryRole;
	}
}
