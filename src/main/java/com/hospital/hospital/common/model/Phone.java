package com.hospital.hospital.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Phone {

	public Phone() {
	}

	public Phone(String countryCode, String number) {
		this.countryCode = countryCode;
		this.number = number;
	}

	// Telefon ülke kodu ayrı tutulur; örnek +90.
	@Column(name = "phone_country_code", length = 10)
	private String countryCode;

	// Telefonun geri kalan kısmı ayrı tutulur; doğrulama ve formatlama kolaylaşır.
	@Column(name = "phone_number", length = 20)
	private String number;

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
