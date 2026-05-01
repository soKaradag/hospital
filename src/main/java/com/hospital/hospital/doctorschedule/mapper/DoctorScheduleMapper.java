package com.hospital.hospital.doctorschedule.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.doctorschedule.dto.CreateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.hospital.doctorschedule.dto.UpdateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.model.DoctorSchedule;

@Component
public class DoctorScheduleMapper {

	public DoctorSchedule toEntity(CreateDoctorScheduleRequest request) {
		if (request == null) {
			return null;
		}
		DoctorSchedule schedule = new DoctorSchedule();
		schedule.setDayOfWeek(request.getDayOfWeek());
		schedule.setStartTime(request.getStartTime());
		schedule.setEndTime(request.getEndTime());
		schedule.setActive(request.isActive());
		return schedule;
	}

	public void updateEntity(UpdateDoctorScheduleRequest request, DoctorSchedule schedule) {
		if (request == null || schedule == null) {
			return;
		}
		schedule.setDayOfWeek(request.getDayOfWeek());
		schedule.setStartTime(request.getStartTime());
		schedule.setEndTime(request.getEndTime());
		schedule.setActive(request.isActive());
	}

	public DoctorScheduleResponse toResponse(DoctorSchedule schedule) {
		return toResponse(schedule, 0L, 0L);
	}

	public DoctorScheduleResponse toResponse(DoctorSchedule schedule, long leaveCount, long exceptionCount) {
		if (schedule == null) {
			return null;
		}
		DoctorScheduleResponse response = new DoctorScheduleResponse();
		response.setId(schedule.getId());
		if (schedule.getDoctor() != null) {
			response.setDoctorId(schedule.getDoctor().getId());
			response.setDoctorFullName(schedule.getDoctor().getFirstName() + " " + schedule.getDoctor().getLastName());
		}
		response.setDayOfWeek(schedule.getDayOfWeek());
		response.setStartTime(schedule.getStartTime());
		response.setEndTime(schedule.getEndTime());
		response.setActive(schedule.isActive());
		response.setLeaveCount(leaveCount);
		response.setExceptionCount(exceptionCount);
		response.setCreatedAt(schedule.getCreatedAt());
		response.setUpdatedAt(schedule.getUpdatedAt());
		return response;
	}
}
