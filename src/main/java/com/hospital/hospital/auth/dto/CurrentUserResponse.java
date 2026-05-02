package com.hospital.hospital.auth.dto;

import java.util.List;
import java.util.UUID;

// Me endpoint'inden dönen mevcut kullanıcı özetidir.
public class CurrentUserResponse {

	public CurrentUserResponse() {
	}

	public CurrentUserResponse(
			UUID id,
			String username,
			List<String> roles,
			List<String> permissions,
			String firstName,
			String lastName,
			String email) {
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.permissions = permissions;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	private UUID id;
	private String username;
	private List<String> roles;
	private List<String> permissions;
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

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
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
