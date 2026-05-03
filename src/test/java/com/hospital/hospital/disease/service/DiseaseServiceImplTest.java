package com.hospital.hospital.disease.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.disease.mapper.DiseaseMapper;
import com.hospital.hospital.disease.model.Disease;
import com.hospital.hospital.disease.repository.DiseaseCategoryRepository;
import com.hospital.hospital.disease.repository.DiseaseCodeMappingRepository;
import com.hospital.hospital.disease.repository.DiseaseRepository;

@ExtendWith(MockitoExtension.class)
class DiseaseServiceImplTest {

	@Mock
	private DiseaseRepository diseaseRepository;

	@Mock
	private DiseaseMapper diseaseMapper;

	@Mock
	private DiseaseCategoryRepository diseaseCategoryRepository;

	@Mock
	private DiseaseCodeMappingRepository diseaseCodeMappingRepository;

	@InjectMocks
	private DiseaseServiceImpl diseaseService;

	@Test
	void deleteShouldDeactivateDisease() {
		UUID id = UUID.randomUUID();
		Disease disease = new Disease();
		disease.setId(id);
		disease.setActive(true);

		when(diseaseRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(disease));

		diseaseService.delete(id);

		verify(diseaseRepository).save(disease);
	}
}
