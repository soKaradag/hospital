package com.hospital.hospital.accesscontrol.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {

	@NotBlank
	@Size(max = 100)
	private String username;

	@NotBlank
	@Size(min = 6, max = 255)
	private String password;

	@NotBlank
	@Size(max = 100)
	private String firstName;

	@NotBlank
	@Size(max = 100)
	private String lastName;

	@Email
	@Size(max = 150)
	private String email;

	@Size(max = 10)
	private String phoneCountryCode;

	@Size(max = 20)
	private String phoneNumber;

	@NotEmpty
	private List<UUID> roleIds;

	@NotNull
	private UUID primaryRoleId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<UUID> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<UUID> roleIds) {
		this.roleIds = roleIds;
	}

	public UUID getPrimaryRoleId() {
		return primaryRoleId;
	}

	public void setPrimaryRoleId(UUID primaryRoleId) {
		this.primaryRoleId = primaryRoleId;
	}
}
