package com.hospital.hospital.surgery.model;

import java.math.BigDecimal;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "surgery_supply_template_items")
public class SurgerySupplyTemplateItem extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "surgery_supply_template_id", nullable = false)
	private SurgerySupplyTemplate surgerySupplyTemplate;

	@Column(name = "inventory_item_code", nullable = false, length = 100)
	private String inventoryItemCode;

	@Column(name = "quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal quantity;

	@Column(name = "note", length = 255)
	private String note;

	public SurgerySupplyTemplate getSurgerySupplyTemplate() {
		return surgerySupplyTemplate;
	}

	public void setSurgerySupplyTemplate(SurgerySupplyTemplate surgerySupplyTemplate) {
		this.surgerySupplyTemplate = surgerySupplyTemplate;
	}

	public String getInventoryItemCode() {
		return inventoryItemCode;
	}

	public void setInventoryItemCode(String inventoryItemCode) {
		this.inventoryItemCode = inventoryItemCode;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
