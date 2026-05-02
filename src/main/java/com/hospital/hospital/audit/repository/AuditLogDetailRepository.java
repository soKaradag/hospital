package com.hospital.hospital.audit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.audit.model.AuditLogDetail;

public interface AuditLogDetailRepository extends JpaRepository<AuditLogDetail, UUID> {
}
