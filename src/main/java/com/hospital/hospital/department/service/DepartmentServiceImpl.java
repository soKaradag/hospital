package com.hospital.hospital.department.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.department.dto.CreateDepartmentRequest;
import com.hospital.hospital.department.dto.DepartmentResponse;
import com.hospital.hospital.department.dto.UpdateDepartmentRequest;
import com.hospital.hospital.department.mapper.DepartmentMapper;
import com.hospital.hospital.department.model.Department;
import com.hospital.hospital.department.repository.DepartmentRepository;

// Service katmanı, iş kurallarını ve veri erişimini yönetir.
// Service anatasyonu, bu sınıfın bir Spring Bean olduğunu ve servis katmanında kullanılacağını belirtir.
// DepartmentServiceImpl sınıfı, DepartmentService interface'ini implement eder.
@Service
public class DepartmentServiceImpl implements DepartmentService {

	/*
	- Bu alanlar sınıfın ihtiyaç duyduğu bağımlılıklardır.
	- Spring bu bağımlılıkları Dependency Injection ile dışarıdan verir.
	- Yani sınıf kendi içinde new ile nesne üretmez, hazır nesneyi alır.
	- Bunun sebebi sınıfın sadece kendi iş mantığına odaklanmasını sağlamaktır.
	- Eğer burada new kullansaydık, sınıf hangi nesnenin nasıl oluşturulacağını da bilmek zorunda kalırdı.
	- Bu durum kodu daha sıkı bağlı hale getirir.
	- Sıkı bağlı yapı test yazmayı ve mock kullanmayı zorlaştırır.
	- Farklı bir implementasyona geçmek de daha zor olur.
	- Spring bean yaşam döngüsünü ve yönetimini de daha zor kontrol eder.
	- Dependency Injection ile nesne oluşturma sorumluluğu Spring container'a bırakılır.
	- final kullanımı, bu bağımlılıkların sadece bir kez atanacağını ve sonradan değişmeyeceğini garanti eder.
	- Bu kullanım memory leak oluşturmaz; çünkü burada gereksiz nesne biriktirilmez, sadece yönetilen bean referansları tutulur.
	*/
	private final DepartmentRepository departmentRepository;
	private final DepartmentMapper departmentMapper;

	public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
		this.departmentRepository = departmentRepository;
		this.departmentMapper = departmentMapper;
	}

	@Override
	@Transactional
	public DepartmentResponse create(CreateDepartmentRequest request) {
		if (departmentRepository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Department name already exists: " + request.getName());
		}

		Department department = departmentMapper.toEntity(request);
		return departmentMapper.toResponse(departmentRepository.save(department));
	}

	@Override
	@Transactional
	public DepartmentResponse update(UUID id, UpdateDepartmentRequest request) {
		Department department = getDepartment(id);
		departmentRepository.findByName(request.getName())
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Department name already exists: " + request.getName());
				});

		departmentMapper.updateEntity(request, department);
		return departmentMapper.toResponse(departmentRepository.save(department));
	}

	@Override
	@Transactional(readOnly = true)
	public DepartmentResponse getById(UUID id) {
		return departmentMapper.toResponse(getDepartment(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DepartmentResponse> getAll(Pageable pageable) {
		return departmentRepository.findAll(pageable).map(departmentMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DepartmentResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		return departmentRepository.findAllByNameContainingIgnoreCase(keyword.trim(), pageable)
				.map(departmentMapper::toResponse);
	}

	private Department getDepartment(UUID id) {
		return departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
	}
}
