package com.hospital.hospital.appointment.controller;

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

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;
import com.hospital.hospital.appointment.model.AppointmentStatus;
import com.hospital.hospital.appointment.service.AppointmentService;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.GlobalExceptionHandler;

@WebMvcTest(AppointmentController.class)
@Import(GlobalExceptionHandler.class)
class AppointmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AppointmentService appointmentService;

	@Test
	void getAllShouldPreferSearchOverOtherFilters() throws Exception {
		AppointmentResponse response = new AppointmentResponse();
		response.setId(UUID.randomUUID());
		response.setNotes("Routine control");

		when(appointmentService.search("control", PageRequest.of(0, 20)))
				.thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

		mockMvc.perform(get("/api/appointments")
						.param("search", "control")
						.param("patientId", UUID.randomUUID().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].notes", is("Routine control")))
				.andExpect(jsonPath("$.data.size", is(20)));
	}

	@Test
	void getAllShouldReturnDateRangeResults() throws Exception {
		AppointmentResponse response = new AppointmentResponse();
		response.setId(UUID.randomUUID());
		response.setStatus(AppointmentStatus.SCHEDULED);

		Instant start = Instant.parse("2026-03-27T10:00:00Z");
		Instant end = Instant.parse("2026-03-27T12:00:00Z");

		when(appointmentService.getAllByDateRange(start, end, PageRequest.of(0, 20)))
				.thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

		mockMvc.perform(get("/api/appointments")
						.param("startDateTime", "2026-03-27T10:00:00Z")
						.param("endDateTime", "2026-03-27T12:00:00Z"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].status", is("SCHEDULED")));
	}

	@Test
	void createShouldReturnBusinessRuleViolationWhenServiceThrows() throws Exception {
		when(appointmentService.create(any(CreateAppointmentRequest.class)))
				.thenThrow(new BusinessRuleViolationException("Appointment dateTime cannot be in the past"));

		mockMvc.perform(post("/api/appointments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "patientId": "11111111-1111-1111-1111-111111111111",
							  "doctorId": "22222222-2222-2222-2222-222222222222",
							  "appointmentDateTime": "2026-03-27T12:00:00Z",
							  "status": "SCHEDULED"
							}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
	}

	@Test
	void getByIdShouldReturnAppointmentWhenServiceReturnsData() throws Exception {
		UUID id = UUID.randomUUID();
		AppointmentResponse response = new AppointmentResponse();
		response.setId(id);
		response.setNotes("Follow-up");

		when(appointmentService.getById(id)).thenReturn(response);

		mockMvc.perform(get("/api/appointments/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.data.id", is(id.toString())))
				.andExpect(jsonPath("$.data.notes", is("Follow-up")));
	}

	@Test
	void createShouldReturnValidationErrorWhenRequiredFieldsAreMissing() throws Exception {
		mockMvc.perform(post("/api/appointments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
	}
}
