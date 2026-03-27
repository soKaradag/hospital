package com.hospital.hospital.patient.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import com.hospital.hospital.common.exception.GlobalExceptionHandler;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.service.PatientService;

@WebMvcTest(PatientController.class)
@Import(GlobalExceptionHandler.class)
class PatientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PatientService patientService;

	@Test
	void getByIdShouldReturnNotFoundWhenServiceThrows() throws Exception {
		UUID id = UUID.randomUUID();
		when(patientService.getById(id)).thenThrow(new ResourceNotFoundException("Patient not found"));

		mockMvc.perform(get("/api/patients/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")));
	}

	@Test
	void getAllShouldReturnPagedResponseWhenNoSearchExists() throws Exception {
		PatientResponse response = new PatientResponse();
		response.setId(UUID.randomUUID());
		response.setFirstName("Ayse");
		response.setCreatedAt(Instant.parse("2026-03-27T10:00:00Z"));

		when(patientService.getAll(PageRequest.of(0, 20)))
				.thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

		mockMvc.perform(get("/api/patients"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].firstName", is("Ayse")))
				.andExpect(jsonPath("$.data.totalPages", is(1)))
				.andExpect(jsonPath("$.data.first", is(true)));
	}

	@Test
	void createShouldReturnSuccessWhenRequestIsValid() throws Exception {
		PatientResponse response = new PatientResponse();
		response.setId(UUID.randomUUID());
		response.setFirstName("Ayse");

		when(patientService.create(any(CreatePatientRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/patients")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "firstName": "Ayse",
							  "lastName": "Yilmaz"
							}
							"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.data.firstName", is("Ayse")));
	}

	@Test
	void getAllShouldPreferSearchWhenSearchParameterExists() throws Exception {
		PatientResponse response = new PatientResponse();
		response.setId(UUID.randomUUID());
		response.setFirstName("Ali");

		when(patientService.search("ali", PageRequest.of(0, 20)))
				.thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

		mockMvc.perform(get("/api/patients")
						.param("search", "ali"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].firstName", is("Ali")))
				.andExpect(jsonPath("$.data.size", is(20)));
	}
}
