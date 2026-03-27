package com.hospital.hospital.auth.dto;

import java.util.UUID;

import com.hospital.hospital.auth.model.Role;

// Me endpoint'inden dönen mevcut kullanıcı özetidir.
public class CurrentUserResponse {

	public CurrentUserResponse() {
	}

	public CurrentUserResponse(UUID id, String username, Role role, String firstName, String lastName, String email) {
		this.id = id;
		this.username = username;
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	private UUID id;
	private String username;
	private Role role;
	// İsim alanları user_info tablosundan gelir; auth tablosunu sade tutmak için ayrı tutulurlar.
	private String firstName;
	private String lastName;
	private String email;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
