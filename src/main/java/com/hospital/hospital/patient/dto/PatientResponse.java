package com.hospital.hospital.patient.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.hospital.hospital.common.dto.AddressDto;
import com.hospital.hospital.common.dto.ContactDto;
import com.hospital.hospital.patient.model.Gender;

public class PatientResponse {

	private UUID id;
	private String firstName;
	private String lastName;
	private String nationalId;
	private LocalDate birthDate;
	private Gender gender;
	private ContactDto contact;
	private AddressDto address;
	private long emergencyContactCount;
	private long insuranceCount;
	private Instant createdAt;
	private Instant updatedAt;

	public PatientResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public ContactDto getContact() {
		return contact;
	}

	public void setContact(ContactDto contact) {
		this.contact = contact;
	}

	public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}

	public long getEmergencyContactCount() {
		return emergencyContactCount;
	}

	public void setEmergencyContactCount(long emergencyContactCount) {
		this.emergencyContactCount = emergencyContactCount;
	}

	public long getInsuranceCount() {
		return insuranceCount;
	}

	public void setInsuranceCount(long insuranceCount) {
		this.insuranceCount = insuranceCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
