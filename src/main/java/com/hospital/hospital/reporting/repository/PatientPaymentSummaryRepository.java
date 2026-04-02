package com.hospital.hospital.reporting.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.hospital.hospital.payment.model.Payment;

/*
- Bu repository native SQL aggregation kullanır.
- Spring Data JPA'da çoğu basit sorgu method ismi türetimi ile çözülebilir.
- Örneğin findById veya findAllByDoctorId gibi metodlar otomatik SQL üretir.
- Ancak burada ihtiyaç daha karmaşıktır:
- patients ve payments tablolarını join etmek,
- hasta bazında group by yapmak,
- count, sum ve max gibi aggregate fonksiyonlar kullanmak,
- ayrıca ödeme durumu PAID olan kayıtları ayrı hesaplamak gerekir.
- Bu tür sorgular method ismi türetimiyle hem zor okunur hem de bakımı zorlaşır.
- Bu nedenle @Query(nativeQuery = true) tercih edilmiştir.
- nativeQuery = true demek, yazılan sorgunun JPQL değil doğrudan veritabanı SQL'i olduğu anlamına gelir.
- Yani burada çalışan sorgu MySQL/H2 gibi JDBC katmanına daha yakın gerçek SQL'dir.
- Bu repository'nin amacı hasta bazında şu özeti çıkarmaktır:
- kaç ödeme kaydı var,
- toplam ödenmiş tutar ne kadar,
- son ödeme tarihi nedir.
- Sonuç doğrudan entity listesi olarak değil, projection interface üzerinden okunur.
- Bunun sebebi bu sorgunun Payment entity'sinin birebir satırlarını değil, özetlenmiş bir veri setini üretmesidir.
- Güvenlik açısından burada SQL injection riski oluşmaz.
- Çünkü sorgu sabit bir SQL metnidir ve kullanıcıdan gelen veri string birleştirme ile query içine eklenmez.
- SQL injection riski genelde dışarıdan gelen input'un "..." + input + "..." şeklinde query string'ine gömülmesiyle ortaya çıkar.
- Bu repository'de böyle bir dinamik üretim yoktur.
- İleride native query'ye filtre parametresi eklenirse de string birleştirme yerine JPA parametre bağlama mekanizması kullanılmalıdır.
- Örneğin :patientId veya ?1 gibi bağlı parametreler kullanmak güvenli yaklaşımdır.
*/
public interface PatientPaymentSummaryRepository extends Repository<Payment, UUID> {

	// Bu sorgu her hasta için tek satır üretir.
	// left join kullanımı, hiç ödeme kaydı olmayan hastaların da raporda görünmesini sağlar.
	// count(pay.id) toplam ödeme kaydı adedini hesaplar.
	// sum(case when ...) sadece PAID durumundaki ödemeleri toplam tutara dahil eder.
	// max(pay.paid_at) ilgili hasta için son tahsilat zamanını verir.
	@Query(value = """
			select
			    p.id as patientId,
			    concat(p.first_name, ' ', p.last_name) as patientFullName,
			    count(pay.id) as paymentCount,
			    coalesce(sum(case when pay.payment_status = 'PAID' then pay.amount else 0 end), 0) as totalPaidAmount,
			    max(pay.paid_at) as lastPaidAt
			from patients p
			left join payments pay on pay.patient_id = p.id
			group by p.id, p.first_name, p.last_name
			order by totalPaidAmount desc, patientFullName asc
			""", nativeQuery = true)
	// Projection dönüşü, sadece ihtiyaç duyulan kolonları küçük ve okunabilir bir yapı ile taşır.
	List<PatientPaymentSummaryProjection> findPatientPaymentSummaries();
}
