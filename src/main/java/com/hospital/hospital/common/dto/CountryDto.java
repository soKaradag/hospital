package com.hospital.hospital.common.dto;

import jakarta.validation.constraints.Size;

// Ülke bilgisini request ve response katmanında ortak taşımak için kullanılır.
public class CountryDto {

	@Size(max = 10)
	private String code;

	@Size(max = 100)
	private String name;

	public CountryDto() {
	}

	public CountryDto(String code, String name) {
		this.code = code;
		this.name = name;
	}

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
}
