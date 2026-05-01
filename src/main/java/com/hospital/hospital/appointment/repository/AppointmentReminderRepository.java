package com.hospital.hospital.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.appointment.model.AppointmentReminder;

public interface AppointmentReminderRepository extends JpaRepository<AppointmentReminder, UUID> {

	long countByAppointmentId(UUID appointmentId);

	Optional<AppointmentReminder> findByAppointmentIdAndReminderType(UUID appointmentId, String reminderType);
}
