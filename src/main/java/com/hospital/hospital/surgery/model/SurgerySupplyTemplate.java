package com.hospital.hospital.surgery.model;

import java.util.ArrayList;
import java.util.List;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "surgery_supply_templates")
public class SurgerySupplyTemplate extends BaseEntity {

	@Column(name = "code", nullable = false, length = 100, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "procedure_code", nullable = false, length = 100)
	private String procedureCode;

	@Column(name = "active", nullable = false)
	private boolean active;

	@OneToMany(mappedBy = "surgerySupplyTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SurgerySupplyTemplateItem> items = new ArrayList<>();

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

	public String getProcedureCode() {
		return procedureCode;
	}

	public void setProcedureCode(String procedureCode) {
		this.procedureCode = procedureCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<SurgerySupplyTemplateItem> getItems() {
		return items;
	}

	public void setItems(List<SurgerySupplyTemplateItem> items) {
		this.items = items;
	}
}
