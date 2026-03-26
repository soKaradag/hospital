package com.hospital.hospital.common.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

@MappedSuperclass
public abstract class BaseEntity {

	// Parametresiz kurucu metot jpa için gereklidir.
	protected BaseEntity() {
	}

	// Parametreli kurucu metot.
	protected BaseEntity(UUID id) {
		this.id = id;
	}

	// Tüm ana tablolarda ortak kimlik alanını tek yerde toplamak için kullanılır.
	// UUID, veritabanında benzersiz kimlik oluşturmak için kullanılır.
	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	private UUID id;

	// Kaydın ilk oluşturulma zamanını otomatik tutar. Instant UTC zamanı tutar.
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	// Kaydın son güncellenme zamanını otomatik tutar. Instant UTC zamanı tutar.
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	// @PrePersist: Bu anotasyon, bir entity'nin veritabanına kaydedilmeden (persist) 
	// önce çalışacak metodu belirtir. Burada, ID null ise otomatik olarak yeni bir UUID atanır.
	@PrePersist
	protected void prePersist() {
		// ID üretimini uygulama tarafında kontrol etmek için kayıt oluşmadan önce UUID atanır.
		if (id == null) {
			id = UUID.randomUUID();
		}
	}

	// Getters and setters

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
