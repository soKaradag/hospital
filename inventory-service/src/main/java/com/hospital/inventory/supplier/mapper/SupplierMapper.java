package com.hospital.inventory.supplier.mapper;

import org.springframework.stereotype.Component;

import com.hospital.inventory.supplier.dto.CreateSupplierRequest;
import com.hospital.inventory.supplier.dto.SupplierResponse;
import com.hospital.inventory.supplier.dto.UpdateSupplierRequest;
import com.hospital.inventory.supplier.model.Supplier;

@Component
public class SupplierMapper {

	public Supplier toEntity(CreateSupplierRequest request) {
		Supplier supplier = new Supplier();
		updateEntity(request.getCode(), request.getName(), request.getContactPerson(), request.getEmail(),
				request.getPhone(), request.isActive(), supplier);
		return supplier;
	}

	public void updateEntity(UpdateSupplierRequest request, Supplier supplier) {
		updateEntity(request.getCode(), request.getName(), request.getContactPerson(), request.getEmail(),
				request.getPhone(), request.isActive(), supplier);
	}

	private void updateEntity(
			String code,
			String name,
			String contactPerson,
			String email,
			String phone,
			boolean active,
			Supplier supplier) {
		supplier.setCode(code.trim());
		supplier.setName(name.trim());
		supplier.setContactPerson(contactPerson != null ? contactPerson.trim() : null);
		supplier.setEmail(email != null ? email.trim() : null);
		supplier.setPhone(phone != null ? phone.trim() : null);
		supplier.setActive(active);
	}

	public SupplierResponse toResponse(Supplier supplier) {
		SupplierResponse response = new SupplierResponse();
		response.setId(supplier.getId());
		response.setCode(supplier.getCode());
		response.setName(supplier.getName());
		response.setContactPerson(supplier.getContactPerson());
		response.setEmail(supplier.getEmail());
		response.setPhone(supplier.getPhone());
		response.setActive(supplier.isActive());
		response.setCreatedAt(supplier.getCreatedAt());
		response.setUpdatedAt(supplier.getUpdatedAt());
		return response;
	}
}
