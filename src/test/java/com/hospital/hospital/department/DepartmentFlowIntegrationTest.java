package com.hospital.hospital.department;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DepartmentFlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createAndReadFlowShouldWorkAcrossControllerServiceMapperAndRepository() throws Exception {
		mockMvc.perform(post("/api/departments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "name": "Integration Cardiology",
							  "description": "Integration test department"
							}
							"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.data.name", is("Integration Cardiology")));

		mockMvc.perform(get("/api/departments")
						.param("search", "Integration"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name", is("Integration Cardiology")))
				.andExpect(jsonPath("$.data.totalElements", is(1)));
	}
}
