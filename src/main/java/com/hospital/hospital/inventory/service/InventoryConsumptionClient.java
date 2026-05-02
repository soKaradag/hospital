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

	public InventoryConsumptionClient(
			RestClient.Builder restClientBuilder,
			CurrentUserContext currentUserContext,
			@Value("${inventory.service.base-url}") String inventoryBaseUrl,
			@Value("${inventory.prescription.warehouse-code}") String prescriptionWarehouseCode,
			@Value("${inventory.prescription.warehouse-zone-code}") String prescriptionWarehouseZoneCode) {
		this.restClient = restClientBuilder.baseUrl(inventoryBaseUrl).build();
		this.currentUserContext = currentUserContext;
		this.prescriptionWarehouseCode = prescriptionWarehouseCode;
		this.prescriptionWarehouseZoneCode = prescriptionWarehouseZoneCode;
	}

	public InventoryConsumptionResponse consumePrescriptionDispense(PrescriptionItem item, PrescriptionDispense dispense) {
		String accessToken = currentUserContext.getRawAccessToken();
		if (accessToken == null || accessToken.isBlank()) {
			throw new InventorySyncException("Inventory consumption requires the current access token");
		}

		InventoryConsumptionRequest request = new InventoryConsumptionRequest();
		request.setInventoryItemCode(item.getMedication().getCode());
		request.setWarehouseCode(prescriptionWarehouseCode);
		request.setWarehouseZoneCode(prescriptionWarehouseZoneCode);
		request.setQuantity(BigDecimal.valueOf(dispense.getQuantity()));
		request.setReferenceType("prescription_dispense");
		request.setReferenceId(dispense.getId() != null ? dispense.getId().toString() : item.getId().toString());
		request.setNotes("Prescription dispense for prescription " + item.getPrescription().getId());

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
