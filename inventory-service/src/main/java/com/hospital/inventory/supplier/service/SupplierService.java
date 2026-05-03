package com.hospital.inventory.supplier.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.supplier.dto.CreateSupplierRequest;
import com.hospital.inventory.supplier.dto.SupplierResponse;
import com.hospital.inventory.supplier.dto.UpdateSupplierRequest;

public interface SupplierService {

	SupplierResponse create(CreateSupplierRequest request);

	SupplierResponse update(UUID id, UpdateSupplierRequest request);

	SupplierResponse getById(UUID id);

	Page<SupplierResponse> getAll(Pageable pageable);

	Page<SupplierResponse> search(String keyword, Pageable pageable);

	void delete(UUID id);
}
