package com.hospital.hospital.audit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hospital.hospital.audit.model.AuditLog;

// Audit log'ları veritabanında tutmak için kullanılır.
/*
 * Hazır CRUD operasyonları için hazır metodlar repository içinde vardır, örnerk metodlar: 
 * save, findById, findAll, deleteById
*/
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {
}
