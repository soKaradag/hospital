package com.hospital.hospital.accesscontrol.dto;

import java.util.List;
import java.util.UUID;

public class UserDetailResponse {

	private UUID id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneCountryCode;
	private String phoneNumber;
	private String status;
	private String primaryRole;
	private List<String> roles;

	public UserDetailResponse() {
	}

	public UserDetailResponse(UUID id, String username, String firstName, String lastName, String email,
			String phoneCountryCode, String phoneNumber, String status, String primaryRole, List<String> roles) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneCountryCode = phoneCountryCode;
		this.phoneNumber = phoneNumber;
		this.status = status;
		this.primaryRole = primaryRole;
		this.roles = roles;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(String primaryRole) {
		this.primaryRole = primaryRole;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
