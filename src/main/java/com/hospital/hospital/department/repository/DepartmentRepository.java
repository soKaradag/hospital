package com.hospital.hospital.department.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.department.model.Department;

// Department tablosu için veri erişim işlemlerini yönetir.
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

	// Belirli bir isimde bölüm kaydı olup olmadığını kontrol eder.
	// existsByName, Spring Data JPA tarafından otomatik olarak sorguya çevrilir.
	// Query: SELECT COUNT(*) > 0 FROM departments WHERE name = ?
	boolean existsByName(String name);

	// Bölüm adı içinde geçen metne göre arama yapar ve sonuçları sayfalayarak getirir.
	// IgnoreCase, büyük-küçük harf duyarlılığını kaldırır.
	// Containing, LIKE %kelime% benzeri arama üretir.
	// Query: SELECT * FROM departments WHERE LOWER(name) LIKE LOWER('%?%')
	Page<Department> findAllByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
