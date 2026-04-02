package com.hospital.hospital.appointment.repository;

import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;

/*
- Bu repository sınıfı, klasik JPA repository yaklaşımının dışına çıkan veri erişim işlerini taşır.
- Buradaki özel durum stored procedure çağrısıdır.
- Stored procedure, veritabanı içinde saklanan ve tekrar tekrar çağrılabilen SQL tabanlı bir iş akışıdır.
- Normalde bir insert işlemi uygulama tarafından "entity oluştur -> repository.save(...)" şeklinde yapılır.
- Ancak bazı senaryolarda iş kuralını veritabanı seviyesinde göstermek veya merkezi hale getirmek istenir.
- Bu örnekte aynı doktorun aynı tarih-saatte ikinci bir randevu almaması kontrolü procedure içine taşınmıştır.
- Böylece "çakışma kontrolü + insert işlemi" tek veritabanı çağrısında yapılabilir.
- Bu yaklaşım ileri veritabanı dersi açısından değerlidir; çünkü iş mantığının bir kısmı SQL tarafında gösterilmiş olur.
- Burada JpaRepository kullanılmamasının nedeni, JPA'nın stored procedure kullanımını bu senaryoda gereksiz yere dolaylı hale getirmesidir.
- İhtiyaç doğrudan JDBC callable statement seviyesindedir.
- Service katmanı procedure detayını bilmez; yalnızca bu repository üzerinden "uygula ve sonucu dön" davranışını kullanır.
- Bu da mevcut katmanlı mimariyi korur: controller -> service -> repository.
*/
@Repository
public class AppointmentProcedureRepository {

	private final JdbcTemplate jdbcTemplate;

	public AppointmentProcedureRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	- Bu metot veritabanındaki create_appointment_if_available procedure'ünü çağırır.
	- Amaç, insert işleminden önce aynı doktor için aynı appointmentDateTime değerinde çakışma olup olmadığını DB tarafında kontrol etmektir.
	- appointmentId uygulama tarafında üretilir.
	- Bunun sebebi mevcut projede kimlik üretiminin BaseEntity üzerinden Java tarafında yürütülmesidir.
	- Yani procedure sadece insert işini yapar; ID üretim stratejisini değiştirmez.
	- request içindeki patientId, doctorId, datetime, status ve notes alanları sırasıyla procedure parametrelerine yazılır.
	- registerOutParameter(7, Types.BOOLEAN) ile procedure içinden dönecek conflict bilgisi için out parametre tanımlanır.
	- callableStatement.execute() çağrısı procedure'ü gerçekten çalıştırır.
	- Procedure insert yapmışsa out parametrede false beklenir.
	- Procedure çakışma bulmuşsa out parametrede true beklenir.
	- Metodun sonunda Boolean.TRUE.equals(...) kullanımı null güvenliği sağlar.
	*/
	public boolean createAppointmentIfAvailable(UUID appointmentId, CreateAppointmentRequest request) {
		Boolean conflictFound = jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
			// CallableStatement, SQL içindeki procedure veya function çağrıları için kullanılan JDBC nesnesidir.
			try (CallableStatement callableStatement = connection
					.prepareCall("{call create_appointment_if_available(?, ?, ?, ?, ?, ?, ?)}")) {
				// 1. parametre: veritabanına yazılacak appointment kimliği.
				callableStatement.setString(1, appointmentId.toString());
				// 2. parametre: randevunun bağlı olduğu hasta kimliği.
				callableStatement.setString(2, request.getPatientId().toString());
				// 3. parametre: randevunun bağlı olduğu doktor kimliği.
				callableStatement.setString(3, request.getDoctorId().toString());
				// 4. parametre: JDBC tarafında datetime(6) ile uyumlu gönderim için Timestamp kullanılır.
				callableStatement.setTimestamp(4, Timestamp.from(request.getAppointmentDateTime()));
				// 5. parametre: enum veritabanında string tutulduğu için name() gönderilir.
				callableStatement.setString(5, request.getStatus().name());
				// 6. parametre: açıklama/not alanı.
				callableStatement.setString(6, request.getNotes());
				// 7. parametre: procedure'ün conflict bulup bulmadığını döneceği out parametredir.
				callableStatement.registerOutParameter(7, Types.BOOLEAN);

				callableStatement.execute();
				// Procedure tamamlandıktan sonra out parametre okunur.
				return callableStatement.getBoolean(7);
			}
		});

		return Boolean.TRUE.equals(conflictFound);
	}
}
