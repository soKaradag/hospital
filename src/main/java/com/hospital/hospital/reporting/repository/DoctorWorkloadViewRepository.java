package com.hospital.hospital.reporting.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hospital.hospital.reporting.dto.DoctorWorkloadReportResponse;

/*
- Bu repository, veritabanında tanımlanmış bir VIEW üzerinden veri okur.
- View, fiziksel bir tablo değildir.
- View; bir veya birden fazla tablodan gelen veriyi, önceden tanımlanmış bir SELECT sorgusu ile "sanal tablo" gibi sunar.
- Yani uygulama "select * from doctor_workload_view" dediğinde aslında arka tarafta daha karmaşık bir join ve aggregation sorgusu çalışır.
- Bu yaklaşım özellikle raporlama ekranlarında değerlidir.
- Çünkü aynı rapor sorgusunu her yerde tekrar tekrar yazmak yerine veritabanında merkezi bir tanım yapılır.
- Bu projede doctor_workload_view doktor bazında:
- appointment sayısını,
- encounter sayısını,
- prescription sayısını
- tek bir satırda birleştirir.
- Böylece Java tarafı yalnızca view'den okuma yapar; join ve group by detayını tekrar etmez.
- JdbcTemplate kullanımı burada bilinçlidir.
- Çünkü view üzerinden gelen veri klasik entity yaşam döngüsüne tam oturmak zorunda değildir.
- Repository görevi yalnızca SQL sonucunu DTO'ya dönüştürmektir.
*/
@Repository
public class DoctorWorkloadViewRepository {

	// Bu SQL ifadesi doğrudan view'den okuma yapar.
	// View zaten karmaşık hesaplamayı içerdiği için burada ek join veya aggregation yoktur.
	private static final String DOCTOR_WORKLOAD_SQL = """
			select
			    doctor_id,
			    doctor_full_name,
			    department_name,
			    appointment_count,
			    encounter_count,
			    prescription_count
			from doctor_workload_view
			order by doctor_full_name asc
			""";

	private final JdbcTemplate jdbcTemplate;

	public DoctorWorkloadViewRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// findAll metodu, view içindeki tüm satırları okuyup rapor DTO listesine çevirir.
	// order by doctor_full_name asc ile sunum tarafında stabil ve okunabilir bir sıra korunur.
	public List<DoctorWorkloadReportResponse> findAll() {
		return jdbcTemplate.query(DOCTOR_WORKLOAD_SQL, this::mapRow);
	}

	// mapRow, JDBC ResultSet içindeki tek bir satırı uygulamanın kullandığı response nesnesine dönüştürür.
	// ResultSet verisi string / number gibi JDBC tipleriyle gelir; burada domain'e yakın DTO tiplerine çevrilir.
	private DoctorWorkloadReportResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return new DoctorWorkloadReportResponse(
				// doctor_id view tarafında varchar(36) geldiği için UUID'ye dönüştürülür.
				UUID.fromString(resultSet.getString("doctor_id")),
				resultSet.getString("doctor_full_name"),
				resultSet.getString("department_name"),
				resultSet.getLong("appointment_count"),
				resultSet.getLong("encounter_count"),
				resultSet.getLong("prescription_count"));
	}
}
