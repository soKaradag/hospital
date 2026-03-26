package com.hospital.hospital.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

// Adres bilgisini request ve response katmanında ortak taşımak için kullanılır.
public class AddressDto {

	@Valid
	private CountryDto country;

	@Valid
	private CityDto city;

	@Size(max = 100)
	private String district;

	@Size(max = 20)
	private String postalCode;

	@Size(max = 500)
	private String addressLine;

	public AddressDto() {
	}

	public AddressDto(CountryDto country, CityDto city, String district, String postalCode, String addressLine) {
		this.country = country;
		this.city = city;
		this.district = district;
		this.postalCode = postalCode;
		this.addressLine = addressLine;
	}

	public CountryDto getCountry() {
		return country;
	}

	public void setCountry(CountryDto country) {
		this.country = country;
	}

	public CityDto getCity() {
		return city;
	}

	public void setCity(CityDto city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
}
