package com.hospital.hospital.common.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;

//Embeddable: Bu anotasyon, Address sınıfının ayrı bir tablo olarak saklanmayacağını, 
//bunun yerine, bu sınıfı kullanan diğer entity'lerin tablolarına gömüleceğini belirtir.
@Embeddable
public class Address {

	public Address() {
	}

	public Address(Country country, City city, String district, String postalCode, String addressLine) {
		this.country = country;
		this.city = city;
		this.district = district;
		this.postalCode = postalCode;
		this.addressLine = addressLine;
	}

	@Embedded
	//AttributeOverrides: Bu anotasyon, gömülü nesnelerin sütun adlarını belirtmek için kullanılır.
	//Burada Country sınıfının code ve name alanlarının, Address sınıfının country_code ve country_name sütunlarına karşılık geldiğini belirtiyoruz.	
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "country_code", length = 10)),
			@AttributeOverride(name = "name", column = @Column(name = "country_name", length = 100))
	})
	private Country country;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "city_code", length = 20)),
			@AttributeOverride(name = "name", column = @Column(name = "city_name", length = 100))
	})
	private City city;

	@Column(name = "district", length = 100)
	private String district;

	@Column(name = "postal_code", length = 20)
	private String postalCode;

	@Column(name = "address_line", length = 500)
	private String addressLine;

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
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
