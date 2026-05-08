package com.hospital.hospital.audit.dto;

import java.util.UUID;

public class AuditLogDetailResponse {

	private UUID id;
	private String key;
	private String value;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
