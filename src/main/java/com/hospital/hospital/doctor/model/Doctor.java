package com.hospital.hospital.doctor.model;

import com.hospital.hospital.common.model.Contact;
import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.department.model.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctors")
public class Doctor extends BaseEntity {

	public Doctor() {
	}

	public Doctor(String firstName, String lastName, String specialization, Contact contact, Department department) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.specialization = specialization;
		this.contact = contact;
		this.department = department;
	}

	// Doktorun temel kimlik bilgileri ayrı alanlarda tutulur.
	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	// Uzmanlık bilgisi bölüm bilgisinden ayrı tutulur; ileride genişletilebilir.
	@Column(name = "specialization", length = 100)
	private String specialization;

	@Embedded
	private Contact contact;

	// ManyToOne burada birden fazla doktorun aynı bölüme bağlı olabileceğini anlatır.
	// FetchType.LAZY: Department verisi sadece ihtiyaç duyulduğunda veritabanından çekilir.
	// optional = false: Department alanı zorunludur, null olamaz.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise doctors tablosundaki department_id kolonunun foreign key olduğunu belirtir.
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

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

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
}
