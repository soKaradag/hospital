package com.hospital.hospital.common.dto;

import jakarta.validation.constraints.Size;

// Telefon bilgisini request ve response katmanında ortak taşımak için kullanılır.
public class PhoneDto {

	@Size(max = 10)
	private String countryCode;

	@Size(max = 20)
	private String number;

	public PhoneDto() {
	}

	public PhoneDto(String countryCode, String number) {
		this.countryCode = countryCode;
		this.number = number;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
