package com.hospital.hospital.doctorschedule.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.doctorschedule.mapper.DoctorScheduleMapper;
import com.hospital.hospital.doctorschedule.model.DoctorSchedule;
import com.hospital.hospital.doctorschedule.repository.DoctorLeaveRepository;
import com.hospital.hospital.doctorschedule.repository.DoctorScheduleExceptionRepository;
import com.hospital.hospital.doctorschedule.repository.DoctorScheduleRepository;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

	@Mock
	private DoctorScheduleRepository doctorScheduleRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private DoctorScheduleMapper doctorScheduleMapper;

	@Mock
	private DoctorLeaveRepository doctorLeaveRepository;

	@Mock
	private DoctorScheduleExceptionRepository doctorScheduleExceptionRepository;

	@InjectMocks
	private DoctorScheduleServiceImpl doctorScheduleService;

	@Test
	void deleteShouldDeactivateSchedule() {
		UUID id = UUID.randomUUID();
		DoctorSchedule schedule = new DoctorSchedule();
		schedule.setId(id);
		schedule.setActive(true);

		when(doctorScheduleRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(schedule));

		doctorScheduleService.delete(id);

		verify(doctorScheduleRepository).save(schedule);
	}
}
