package com.hospital.hospital.common.dto;

import jakarta.validation.constraints.Size;

// Şehir bilgisini request ve response katmanında ortak taşımak için kullanılır.
public class CityDto {

	@Size(max = 20)
	private String code;

	@Size(max = 100)
	private String name;

	public CityDto() {
	}

	public CityDto(String code, String name) {
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
