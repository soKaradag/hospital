package com.hospital.hospital.surgery.dto;

import jakarta.validation.constraints.Size;

public class UpdateSurgeryLifecycleRequest {

	@Size(max = 255, message = "note must be at most 255 characters")
	private String note;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
