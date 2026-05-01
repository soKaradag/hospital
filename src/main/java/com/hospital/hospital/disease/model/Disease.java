package com.hospital.hospital.disease.model;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
- Disease entity, sistemde tekrar kullanılacak hastalık sözlüğünü temsil eder.
- Hasta geçmişi ve encounter teşhisleri bu katalogdaki kayıtlarla ilişki kurabilir.
*/
@Entity
@Table(name = "diseases")
public class Disease extends BaseEntity {

	public Disease() {
	}

	public Disease(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	@Column(name = "code", nullable = false, unique = true, length = 50)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private DiseaseCategory category;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DiseaseCategory getCategory() {
		return category;
	}

	public void setCategory(DiseaseCategory category) {
		this.category = category;
	}
}
