package com.hospital.hospital.surgery.service;

import java.util.UUID;

import com.hospital.hospital.surgery.dto.CreateOperatingRoomRequest;
import com.hospital.hospital.surgery.dto.CreateSurgeryRequestRequest;
import com.hospital.hospital.surgery.dto.OperatingRoomResponse;
import com.hospital.hospital.surgery.dto.ScheduleSurgeryRequest;
import com.hospital.hospital.surgery.dto.SurgeryRequestResponse;
import com.hospital.hospital.surgery.dto.SurgeryResponse;

public interface SurgeryService {

	OperatingRoomResponse createOperatingRoom(CreateOperatingRoomRequest request);

	SurgeryRequestResponse createSurgeryRequest(CreateSurgeryRequestRequest request);

	SurgeryResponse scheduleSurgery(ScheduleSurgeryRequest request);

	SurgeryResponse getSurgeryById(UUID id);
}
