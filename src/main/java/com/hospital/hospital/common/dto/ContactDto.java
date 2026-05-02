package com.hospital.hospital.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

// İletişim bilgisini request ve response katmanında ortak taşımak için kullanılır.
public class ContactDto {

	@Valid
	private PhoneDto phone;

	@Email
	private String email;

	public ContactDto() {
	}

	public ContactDto(PhoneDto phone, String email) {
		this.phone = phone;
		this.email = email;
	}

	public PhoneDto getPhone() {
		return phone;
	}

	public void setPhone(PhoneDto phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
