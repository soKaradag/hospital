package com.hospital.hospital.department.model;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

//Department: Hastanenin bölümlerini temsil eder. BaseEntity'den türediği için id, createdAt ve updatedAt alanlarına sahiptir.
//Entity: Bu anotasyon, Department sınıfının bir veritabanı tablosu olarak saklanacağını belirtir.
//Table: Bu anotasyon, Department sınıfının veritabanındaki tablosunun adını belirtir.
@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

	public Department() {
	}

	public Department(String name, String description) {
		this.name = name;
		this.description = description;
	}

	// Bölüm adı kullanıcıya gösterilen temel alandır ve zorunludur.
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	// Bölüm hakkında kısa açıklama tutmak için eklenmiştir.
	@Column(name = "description", length = 255)
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
