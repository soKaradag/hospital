package com.hospital.hospital.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Country {

	public Country() {
	}

	public Country(String code, String name) {
		this.code = code;
		this.name = name;
	}

	// Ülke kodu raporlama ve standartlaştırma için ayrı tutulur.
	@Column(name = "country_code", length = 10)
	private String code;

	// Ülke adı kullanıcıya gösterim için tutulur.
	@Column(name = "country_name", length = 100)
	private String name;

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
