package com.hospital.hospital.reporting.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hospital.hospital.reporting.dto.PaymentAuditResponse;

/*
- Bu repository, trigger ile doldurulan payment_audit tablosunu okur.
- Trigger, veritabanında belirli bir olay gerçekleştiğinde otomatik çalışan SQL kuralıdır.
- Bu projede payments tablosuna yeni kayıt eklendiğinde AFTER INSERT trigger'ı devreye girer.
- Trigger ilgili ödeme bilgisinin bir kopyasını payment_audit tablosuna yazar.
- Böylece "hangi ödeme ne zaman oluştu" sorusu için ayrı bir history izi tutulmuş olur.
- Bu yaklaşım ileri veritabanı tarafında önemlidir; çünkü veri değişimi uygulama kodundan bağımsız olarak da izlenebilir hale gelir.
- Buradaki repository'nin görevi, trigger ile oluşmuş bu audit kayıtlarını uygulama tarafından okunabilir hale getirmektir.
- JdbcTemplate tercih edilmesinin nedeni audit tablosunun yalnızca raporlama/izleme amacı taşıması ve entity yaşam döngüsüne ihtiyaç duymamasıdır.
- Yani burada hedef tam bir domain modeli değil, okunabilir bir audit response listesidir.
*/
@Repository
public class PaymentAuditRepository {

	private final JdbcTemplate jdbcTemplate;

	public PaymentAuditRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// findRecent metodu audit tablosundaki en güncel kayıtları getirir.
	// limit parametresi sayesinde rapor ekranı veya demo anlatımı için kontrollü sayıda satır okunur.
	// order by logged_at desc ile en yeni kayıt üstte tutulur.
	public List<PaymentAuditResponse> findRecent(int limit) {
		return jdbcTemplate.query(
				"""
						select
						    id,
						    payment_id,
						    patient_id,
						    encounter_id,
						    action,
						    amount,
						    currency,
						    payment_method,
						    payment_status,
						    paid_at,
						    logged_at
						from payment_audit
						order by logged_at desc
						limit ?
						""",
				this::mapRow,
				limit);
	}

	// mapRow, audit tablosundan gelen ham JDBC verisini PaymentAuditResponse nesnesine çevirir.
	// Burada string olarak gelen UUID alanları tekrar UUID tipine dönüştürülür.
	// paid_at nullable olduğu için null kontrolü ayrıca yapılır.
	private PaymentAuditResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return new PaymentAuditResponse(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("payment_id")),
				UUID.fromString(resultSet.getString("patient_id")),
				UUID.fromString(resultSet.getString("encounter_id")),
				resultSet.getString("action"),
				resultSet.getBigDecimal("amount"),
				resultSet.getString("currency"),
				resultSet.getString("payment_method"),
				resultSet.getString("payment_status"),
				resultSet.getTimestamp("paid_at") != null ? resultSet.getTimestamp("paid_at").toInstant() : null,
				resultSet.getTimestamp("logged_at").toInstant());
	}
}
