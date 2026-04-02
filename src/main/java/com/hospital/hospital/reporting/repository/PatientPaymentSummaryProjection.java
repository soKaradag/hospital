package com.hospital.hospital.reporting.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

/*
- Projection, entity olmayan sorgu sonuçlarını hafif bir arayüz üzerinden almak için kullanılan bir yaklaşımdır.
- Buradaki sorgu "özet" veri üretir; yani doğrudan Payment entity'sinin kendisini döndürmez.
- Örneğin patientFullName veya totalPaidAmount gibi alanlar hesaplanmış alanlardır.
- Bu nedenle sonuç setini tam bir entity'ye map etmek doğru olmaz.
- Interface projection yaklaşımında Spring Data, sorgudaki alias isimlerine bakarak bu getter'ları doldurur.
- Örneğin sorguda "p.id as patientId" yazıldığı için getPatientId() değeri otomatik doldurulur.
- Bu yaklaşım hem performans hem okunabilirlik açısından pratiktir.
- Ayrıca gereksiz entity yüklemelerini de engeller.
*/
public interface PatientPaymentSummaryProjection {

	String getPatientId();

	String getPatientFullName();

	Long getPaymentCount();

	BigDecimal getTotalPaidAmount();

	Timestamp getLastPaidAt();
}
