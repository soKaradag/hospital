package com.hospital.hospital.reporting.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.reporting.model.ReportExportJob;

public interface ReportExportJobRepository extends JpaRepository<ReportExportJob, UUID> {
}
