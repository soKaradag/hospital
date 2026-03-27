package com.hospital.hospital.disease.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.disease.dto.CreateDiseaseRequest;
import com.hospital.hospital.disease.dto.DiseaseResponse;
import com.hospital.hospital.disease.dto.UpdateDiseaseRequest;

/*
- Bu servis sözleşmesi hastalık katalogu akışlarını tanımlar.
- Controller yalnızca bu arayüzü bilir; veri erişim ve iş kuralları implementation içinde kalır.
- Böylece katalog yönetimi ileride başka modüller tarafından da aynı sözleşme ile kullanılabilir.
*/
public interface DiseaseService {

	// Yeni hastalık katalog kaydı oluşturur.
	DiseaseResponse create(CreateDiseaseRequest request);

	// Mevcut hastalık katalog kaydını günceller.
	DiseaseResponse update(UUID id, UpdateDiseaseRequest request);

	// Tekil hastalık kaydını getirir.
	DiseaseResponse getById(UUID id);

	// Tüm hastalık kayıtlarını sayfalı şekilde listeler.
	Page<DiseaseResponse> getAll(Pageable pageable);

	// Kod veya isim alanına göre arama yapar.
	Page<DiseaseResponse> search(String keyword, Pageable pageable);
}
