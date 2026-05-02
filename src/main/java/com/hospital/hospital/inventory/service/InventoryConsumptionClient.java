package com.hospital.hospital.inventory.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.inventory.dto.InventoryConsumptionRequest;
import com.hospital.hospital.inventory.dto.InventoryConsumptionResponse;
import com.hospital.hospital.encounter.model.EncounterProcedure;
import com.hospital.hospital.inventory.exception.InventoryShortageException;
import com.hospital.hospital.inventory.exception.InventorySyncException;
import com.hospital.hospital.prescription.model.PrescriptionDispense;
import com.hospital.hospital.prescription.model.PrescriptionItem;

@Service
public class InventoryConsumptionClient {

	private static final ParameterizedTypeReference<ApiResponse<InventoryConsumptionResponse>> RESPONSE_TYPE =
			new ParameterizedTypeReference<>() {
			};

	private final RestClient restClient;
	private final CurrentUserContext currentUserContext;
	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
	private final String prescriptionWarehouseCode;
	private final String prescriptionWarehouseZoneCode;
	private final String procedureWarehouseCode;
	private final String procedureWarehouseZoneCode;

	public InventoryConsumptionClient(
			RestClient.Builder restClientBuilder,
			CurrentUserContext currentUserContext,
			@Value("${inventory.service.base-url}") String inventoryBaseUrl,
			@Value("${inventory.prescription.warehouse-code}") String prescriptionWarehouseCode,
			@Value("${inventory.prescription.warehouse-zone-code}") String prescriptionWarehouseZoneCode,
			@Value("${inventory.procedure.warehouse-code}") String procedureWarehouseCode,
			@Value("${inventory.procedure.warehouse-zone-code}") String procedureWarehouseZoneCode) {
		this.restClient = restClientBuilder.baseUrl(inventoryBaseUrl).build();
		this.currentUserContext = currentUserContext;
		this.prescriptionWarehouseCode = prescriptionWarehouseCode;
		this.prescriptionWarehouseZoneCode = prescriptionWarehouseZoneCode;
		this.procedureWarehouseCode = procedureWarehouseCode;
		this.procedureWarehouseZoneCode = procedureWarehouseZoneCode;
	}

	public InventoryConsumptionResponse consumePrescriptionDispense(PrescriptionItem item, PrescriptionDispense dispense) {
		return consume(
				item.getMedication().getCode(),
				prescriptionWarehouseCode,
				prescriptionWarehouseZoneCode,
				BigDecimal.valueOf(dispense.getQuantity()),
				"prescription_dispense",
				dispense.getId() != null ? dispense.getId().toString() : item.getId().toString(),
				"Prescription dispense for prescription " + item.getPrescription().getId());
	}

	public InventoryConsumptionResponse consumeEncounterProcedure(EncounterProcedure procedure) {
		return consume(
				procedure.getProcedureCode(),
				procedureWarehouseCode,
				procedureWarehouseZoneCode,
				BigDecimal.ONE,
				"encounter_procedure",
				procedure.getId() != null ? procedure.getId().toString() : procedure.getEncounter().getId().toString(),
				"Encounter procedure for encounter " + procedure.getEncounter().getId());
	}

	private InventoryConsumptionResponse consume(
			String inventoryItemCode,
			String warehouseCode,
			String warehouseZoneCode,
			BigDecimal quantity,
			String referenceType,
			String referenceId,
			String notes) {
		String accessToken = currentUserContext.getRawAccessToken();
		if (accessToken == null || accessToken.isBlank()) {
			throw new InventorySyncException("Inventory consumption requires the current access token");
		}

		InventoryConsumptionRequest request = new InventoryConsumptionRequest();
		request.setInventoryItemCode(inventoryItemCode);
		request.setWarehouseCode(warehouseCode);
		request.setWarehouseZoneCode(warehouseZoneCode);
		request.setQuantity(quantity);
		request.setReferenceType(referenceType);
		request.setReferenceId(referenceId);
		request.setNotes(notes);

		try {
			ApiResponse<InventoryConsumptionResponse> response = restClient.post()
					.uri("/api/inventory/consumptions")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.body(request)
					.retrieve()
					.onStatus(this::isShortageStatus, (clientRequest, clientResponse) -> {
						throw new InventoryShortageException(readErrorMessage(clientResponse));
					})
					.onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
						throw new InventorySyncException(readErrorMessage(clientResponse));
					})
					.body(RESPONSE_TYPE);

			if (response == null || !response.isSuccess() || response.getData() == null) {
				throw new InventorySyncException("Inventory service returned an empty consumption response");
			}
			return response.getData();
		} catch (InventoryShortageException | InventorySyncException exception) {
			throw exception;
		} catch (RestClientException exception) {
			throw new InventorySyncException("Inventory service could not be reached", exception);
		}
	}

	private boolean isShortageStatus(HttpStatusCode statusCode) {
		return statusCode.value() == 400 || statusCode.value() == 409 || statusCode.value() == 422;
	}

	private String readErrorMessage(ClientHttpResponse response) {
		try {
			String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
			if (body == null || body.isBlank()) {
				return buildFallbackMessage(response);
			}
			JsonNode node = objectMapper.readTree(body);
			JsonNode messageNode = node.get("message");
			if (messageNode != null && messageNode.isTextual()) {
				return messageNode.asText();
			}
			return body;
		} catch (Exception exception) {
			return buildFallbackMessage(response);
		}
	}

	private String buildFallbackMessage(ClientHttpResponse response) {
		try {
			return "Inventory request failed with status " + response.getStatusCode().value();
		} catch (Exception exception) {
			return "Inventory request failed";
		}
	}
}
