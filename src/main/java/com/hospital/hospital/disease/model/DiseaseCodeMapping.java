package com.hospital.hospital.disease.model;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "disease_code_mappings")
public class DiseaseCodeMapping extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "disease_id", nullable = false)
	private Disease disease;

	@Column(name = "coding_system", nullable = false, length = 100)
	private String codingSystem;

	@Column(name = "external_code", nullable = false, length = 100)
	private String externalCode;

	@Column(name = "description", length = 255)
	private String description;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getCodingSystem() {
		return codingSystem;
	}

	public void setCodingSystem(String codingSystem) {
		this.codingSystem = codingSystem;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
