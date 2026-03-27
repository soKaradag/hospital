package com.hospital.hospital.department.controller;

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

import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.GlobalExceptionHandler;
import com.hospital.hospital.department.dto.CreateDepartmentRequest;
import com.hospital.hospital.department.dto.DepartmentResponse;
import com.hospital.hospital.department.service.DepartmentService;

@WebMvcTest(DepartmentController.class)
@Import(GlobalExceptionHandler.class)
class DepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DepartmentService departmentService;

	@Test
	void createShouldReturnValidationErrorWhenNameIsBlank() throws Exception {
		mockMvc.perform(post("/api/departments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "name": ""
							}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
				.andExpect(jsonPath("$.errors[0].field", is("name")));
	}

	@Test
	void createShouldReturnConflictWhenDuplicateResourceThrown() throws Exception {
		when(departmentService.create(any(CreateDepartmentRequest.class)))
				.thenThrow(new DuplicateResourceException("Department name already exists"));

		mockMvc.perform(post("/api/departments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "name": "Cardiology"
							}
							"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("DUPLICATE_RESOURCE")));
	}

	@Test
	void getAllShouldReturnPagedResponseWhenSearchIsUsed() throws Exception {
		DepartmentResponse response = new DepartmentResponse();
		response.setId(UUID.randomUUID());
		response.setName("Cardiology");
		response.setCreatedAt(Instant.parse("2026-03-27T10:00:00Z"));

		when(departmentService.search("card", PageRequest.of(0, 20)))
				.thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

		mockMvc.perform(get("/api/departments").param("search", "card"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.data.content[0].name", is("Cardiology")))
				.andExpect(jsonPath("$.data.page", is(0)))
				.andExpect(jsonPath("$.data.size", is(20)))
				.andExpect(jsonPath("$.data.totalElements", is(1)));
	}

	@Test
	void getByIdShouldReturnDepartmentWhenServiceReturnsData() throws Exception {
		UUID id = UUID.randomUUID();
		DepartmentResponse response = new DepartmentResponse();
		response.setId(id);
		response.setName("Radiology");

		when(departmentService.getById(id)).thenReturn(response);

		mockMvc.perform(get("/api/departments/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.data.id", is(id.toString())))
				.andExpect(jsonPath("$.data.name", is("Radiology")));
	}
}
