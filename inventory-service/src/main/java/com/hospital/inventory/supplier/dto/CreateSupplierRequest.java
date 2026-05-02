package com.hospital.inventory.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSupplierRequest {

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@NotBlank(message = "name must not be blank")
	@Size(max = 150, message = "name must be at most 150 characters")
	private String name;

	@Size(max = 120, message = "contactPerson must be at most 120 characters")
	private String contactPerson;

	@Email(message = "email must be valid")
	@Size(max = 150, message = "email must be at most 150 characters")
	private String email;

	@Size(max = 40, message = "phone must be at most 40 characters")
	private String phone;

	private boolean active = true;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
