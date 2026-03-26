package com.hospital.hospital.patient.model;

import java.time.LocalDate;

import com.hospital.hospital.common.model.Address;
import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.common.model.Contact;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient extends BaseEntity {

	public Patient() {
	}

	public Patient(String firstName, String lastName, String nationalId, LocalDate birthDate, Gender gender,
			Contact contact, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.nationalId = nationalId;
		this.birthDate = birthDate;
		this.gender = gender;
		this.contact = contact;
		this.address = address;
	}

	// Hastanın temel kimlik alanları ayrı saklanır; arama ve listeleme kolaylaşır.
	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	// TCKN veya benzeri ulusal kimlik değeri için ayrıldı; her hastada zorunlu olmayabilir.
	@Column(name = "national_id", length = 20, unique = true)
	private String nationalId;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	// Cinsiyet alanı raporlama ve kayıt standardı için enum olarak tutulur.
	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 20)
	private Gender gender;

	@Embedded
	private Contact contact;

	@Embedded
	private Address address;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
