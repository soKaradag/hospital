package com.hospital.hospital.doctorschedule.controller;

import java.time.DayOfWeek;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.auth.annotation.RequirePermission;
import com.hospital.hospital.auth.model.PermissionCodes;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;
import com.hospital.hospital.doctorschedule.dto.CreateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.hospital.doctorschedule.dto.UpdateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.service.DoctorScheduleService;

import jakarta.validation.Valid;

/*
- Bu controller doktor çalışma planı endpoint'lerini yönetir.
- Yazma işlemleri daha dar rol seti ile sınırlandırılır; okuma işlemleri diğer sağlık personeline de açılabilir.
*/
@Validated
@RestController
@RequestMapping("/api/doctor-schedules")
@RequirePermission(PermissionCodes.DOCTOR_SCHEDULES_READ)
public class DoctorScheduleController {

	private final DoctorScheduleService doctorScheduleService;

	public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
		this.doctorScheduleService = doctorScheduleService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.DOCTOR_SCHEDULES_WRITE)
	public ApiResponse<DoctorScheduleResponse> create(@Valid @RequestBody CreateDoctorScheduleRequest request) {
		return ApiResponse.success("Doctor schedule created successfully", doctorScheduleService.create(request));
	}

	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.DOCTOR_SCHEDULES_WRITE)
	public ApiResponse<DoctorScheduleResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateDoctorScheduleRequest request) {
		return ApiResponse.success("Doctor schedule updated successfully", doctorScheduleService.update(id, request));
	}

	@GetMapping("/{id}")
	public ApiResponse<DoctorScheduleResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Doctor schedule retrieved successfully", doctorScheduleService.getById(id));
	}

	@GetMapping
	public ApiResponse<PageResponse<DoctorScheduleResponse>> getAll(
			@RequestParam(required = false) UUID doctorId,
			@RequestParam(required = false) DayOfWeek dayOfWeek,
			@PageableDefault(size = 20) Pageable pageable) {
		if (doctorId != null) {
			return ApiResponse.success("Doctor schedules retrieved successfully",
					PageResponse.from(doctorScheduleService.getAllByDoctor(doctorId, pageable)));
		}
		if (dayOfWeek != null) {
			return ApiResponse.success("Doctor schedules retrieved successfully",
					PageResponse.from(doctorScheduleService.getAllByDay(dayOfWeek, pageable)));
		}
		return ApiResponse.success("Doctor schedules retrieved successfully",
				PageResponse.from(doctorScheduleService.getAll(pageable)));
	}

	@DeleteMapping("/{id}")
	@RequirePermission(PermissionCodes.DOCTOR_SCHEDULES_WRITE)
	public ApiResponse<Void> delete(@PathVariable UUID id) {
		doctorScheduleService.delete(id);
		return ApiResponse.success("Doctor schedule deleted successfully", null);
	}
}
