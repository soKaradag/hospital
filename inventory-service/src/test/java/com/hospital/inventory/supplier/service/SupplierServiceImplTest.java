package com.hospital.inventory.supplier.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.supplier.mapper.SupplierMapper;
import com.hospital.inventory.supplier.model.Supplier;
import com.hospital.inventory.supplier.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

	@Mock
	private SupplierRepository supplierRepository;

	@Mock
	private SupplierMapper supplierMapper;

	@InjectMocks
	private SupplierServiceImpl supplierService;

	@Test
	void deleteShouldDeactivateSupplier() {
		UUID id = UUID.randomUUID();
		Supplier supplier = new Supplier();
		supplier.setId(id);
		supplier.setActive(true);

		when(supplierRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(supplier));

		supplierService.delete(id);

		verify(supplierRepository).save(supplier);
	}
}
