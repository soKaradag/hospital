package com.hospital.hospital.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

//Embeddable: Bu anotasyon, City sınıfının ayrı bir tablo olarak saklanmayacağını, 
//bunun yerine, bu sınıfı kullanan diğer entity'lerin tablolarına gömüleceğini belirtir.
@Embeddable
public class City {

	public City() {
	}

	public City(String code, String name) {
		this.code = code;
		this.name = name;
	}

	// Şehir kodu ileride referanslama veya entegrasyon ihtiyacı için ayrı tutulur.
	@Column(name = "city_code", length = 20)
	private String code;

	// Şehir adı kullanıcıya gösterim için tutulur.
	@Column(name = "city_name", length = 100)
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
