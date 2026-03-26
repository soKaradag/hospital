package com.hospital.hospital.common.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;

//Embeddable: Bu anotasyon, Contact sınıfının ayrı bir tablo olarak saklanmayacağını, 
//bunun yerine, bu sınıfı kullanan diğer entity'lerin tablolarına gömüleceğini belirtir.
@Embeddable
public class Contact {

	public Contact() {
	}

	public Contact(Phone phone, String email) {
		this.phone = phone;
		this.email = email;
	}

	// Contact ayrı bir entity değildir; farklı domainlerde tekrar kullanılacak iletişim değer nesnesidir.
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "countryCode", column = @Column(name = "phone_country_code", length = 10)),
			@AttributeOverride(name = "number", column = @Column(name = "phone_number", length = 20))
	})
	private Phone phone;

	@Column(name = "email", length = 150)
	private String email;

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
