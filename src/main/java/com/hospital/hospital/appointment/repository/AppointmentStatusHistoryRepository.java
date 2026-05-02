package com.hospital.hospital.appointment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.appointment.model.AppointmentStatusHistory;

public interface AppointmentStatusHistoryRepository extends JpaRepository<AppointmentStatusHistory, UUID> {

	long countByAppointmentId(UUID appointmentId);
}
