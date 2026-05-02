package com.hospital.hospital.reporting.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.reporting.model.ReportSnapshot;

public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, UUID> {
}
