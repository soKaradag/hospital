package com.hospital.inventory.supplier.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.supplier.dto.CreateSupplierRequest;
import com.hospital.inventory.supplier.dto.SupplierResponse;
import com.hospital.inventory.supplier.dto.UpdateSupplierRequest;
import com.hospital.inventory.supplier.mapper.SupplierMapper;
import com.hospital.inventory.supplier.model.Supplier;
import com.hospital.inventory.supplier.repository.SupplierRepository;

@Service
public class SupplierServiceImpl implements SupplierService {

	private final SupplierRepository supplierRepository;
	private final SupplierMapper supplierMapper;

	public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
		this.supplierRepository = supplierRepository;
		this.supplierMapper = supplierMapper;
	}

	@Override
	@Transactional
	public SupplierResponse create(CreateSupplierRequest request) {
		if (supplierRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Supplier code already exists: " + request.getCode());
		}
		return supplierMapper.toResponse(supplierRepository.save(supplierMapper.toEntity(request)));
	}

	@Override
	@Transactional
	public SupplierResponse update(UUID id, UpdateSupplierRequest request) {
		Supplier supplier = getSupplier(id);
		supplierRepository.findByCodeIgnoreCase(request.getCode().trim())
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Supplier code already exists: " + request.getCode());
				});
		supplierMapper.updateEntity(request, supplier);
		return supplierMapper.toResponse(supplierRepository.save(supplier));
	}

	@Override
	@Transactional(readOnly = true)
	public SupplierResponse getById(UUID id) {
		return supplierMapper.toResponse(getSupplier(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SupplierResponse> getAll(Pageable pageable) {
		return supplierRepository.findAllByActiveTrue(pageable).map(supplierMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SupplierResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String trimmedKeyword = keyword.trim();
		return supplierRepository.findAllByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndCodeContainingIgnoreCase(
				trimmedKeyword,
				trimmedKeyword,
				pageable).map(supplierMapper::toResponse);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Supplier supplier = getSupplier(id);
		supplier.setActive(false);
		supplierRepository.save(supplier);
	}

	private Supplier getSupplier(UUID id) {
		return supplierRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
	}
}
