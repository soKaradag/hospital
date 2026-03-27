# Hospital Backend

Bu proje, hastane yonetim sistemi icin gelistirilen Spring Boot tabanli backend uygulamasidir.

Su an proje **Faz 2 foundation** seviyesindedir. Bu fazda Faz 1 cekirdek domainleri korunarak custom auth, JWT tabanli token yapisi, rol bazli yetkilendirme, audit altyapisi ve UI gelistirmeye uygun yeni klinik domainler eklenmistir.

## Faz 2 Kapsami
- Custom authentication altyapisi (`Spring Security` kullanmadan)
- JWT access token ve refresh token yapisi
- `login`, `refresh`, `logout`, `me` auth akislari
- `@RequireRole` ile rol bazli yetkilendirme
- `@Audit` + observer pattern ile audit event ve DB persistence yapisi
- Doktor haftalik calisma planlari
- Hastalik katalogu ve hasta hastalik gecmisi
- Encounter bazli teshis kayitlari
- Recete ust kayitlari

## Kullanilan Teknolojiler
- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- MSSQL surucu destegi
- Bean Validation
- Spring AOP
- `spring-security-crypto` (`BCrypt` icin, auth framework olarak degil)

## Temel Mimari Yaklasim
- Domain bazli klasorleme kullanilir.
- Her domain icinde `controller`, `dto`, `mapper`, `model`, `repository`, `service` katmanlari bulunur.
- Is kurallari controller icinde degil, service katmaninda tutulur.
- Entity siniflari dis dunyaya dogrudan acilmaz; DTO kullanilir.
- Donusumler manuel mapper siniflari ile yapilir.
- Listeleme endpoint'lerinde pagination zorunludur.
- Auth ve audit cross-cutting concern olarak merkezi katmanlarda cozulur.

## Mevcut Domainler
- `auth`
- `audit`
- `department`
- `doctor`
- `doctor-schedule`
- `patient`
- `disease`
- `patient-disease`
- `appointment`
- `encounter`
- `encounter-diagnosis`
- `prescription`
- `payment`
- `common`

## Guvenlik Kararlari
- Auth yapisi tamamen custom tasarlanmistir; `Spring Security` kullanilmaz.
- Roller enum olarak tanimlidir: `ADMIN`, `DOCTOR`, `RECEPTIONIST`, `CASHIER`, `NURSE`
- Sifreler `BCrypt` ile hash'lenir.
- Access token kisa omurlu JWT olarak kullanilir.
- Refresh token DB uzerinden yonetilir ve hash olarak saklanir.
- Bu proje kapsaminda `accessToken` ve `refreshToken` response body icinde doner.
- Yetki kontrolu `@RequireRole` ve merkezi interceptor akisi ile uygulanir.

## Veritabani Kararlari
- Tum `id` alanlari uygulama tarafinda `UUID` olarak tutulur.
- Veritabaninda `id` kolonlari `varchar(36)` olarak saklanir.
- Zaman noktalari UTC mantigi ile yonetilir.
- Sadece tarih gereken alanlarda `LocalDate` kullanilir.
- Sadece saat gereken alanlarda `LocalTime` kullanilir.
- Faz 1 ve Faz 2 schema dosyalari SQL scriptleri ile yonetilir.

## Tablo Durumu
Toplam tablo sayisi su anda `15`:

- Faz 1 tablolar:
  - `departments`
  - `doctors`
  - `patients`
  - `appointments`
  - `encounters`
  - `payments`
- Faz 2 tablolar:
  - `users`
  - `user_info`
  - `refresh_tokens`
  - `audit_logs`
  - `doctor_schedules`
  - `diseases`
  - `patient_diseases`
  - `encounter_diagnoses`
  - `prescriptions`

## API Kararlari
- Basarili cevaplar `ApiResponse<T>` ile doner.
- Hata cevaplari `ApiErrorResponse` ile doner.
- Liste endpoint'leri `PageResponse<T>` kullanir.
- Validation ve is kurali hatalari ortak exception handler uzerinden yonetilir.
- Korumali endpoint'lerde `Authorization: Bearer <accessToken>` kullanilir.

## SQL Yapisi
Faz 1 SQL dosyalari:

`sql/phase-1-foundation/mysql`

Faz 2 SQL dosyalari:

`sql/phase-2-foundation/mysql`

Faz 2 calistirma sirasi:
- `01_auth_users.sql`
- `02_refresh_tokens.sql`
- `03_audit_logs.sql`
- `04_doctor_schedules.sql`
- `05_diseases_patient_diseases.sql`
- `06_encounter_diagnoses_prescriptions.sql`

## Dokumantasyon
Detayli aciklamalar `docs` klasoru altindadir:
- [Faz 1 Ozeti](/Users/serdar/Documents/hospital/docs/phase-1-foundation.md)
- [Faz 2 Gelistirme Adimlari](/Users/serdar/Documents/hospital/docs/phase-2-implementation-steps.md)
- [Mimari Yaklasim](/Users/serdar/Documents/hospital/docs/architecture.md)
- [Design Pattern ve Kavramlar](/Users/serdar/Documents/hospital/docs/design-patterns.md)
- [Proje Yapisi](/Users/serdar/Documents/hospital/docs/project-structure.md)
- [API Kurallari](/Users/serdar/Documents/hospital/docs/api-conventions.md)
- [Faz 2 API Contract](/Users/serdar/Documents/hospital/docs/api-contract-phase-2.md)
- [Tarih ve Saat Kurallari](/Users/serdar/Documents/hospital/docs/date-time-conventions.md)

## Sonraki Yon
- Faz 3 simdilik kapsam disi tutuluyor.
- Bir sonraki pratik adim, UI gelistirmeyi hizlandirmak icin ekran odakli entegrasyon ve API contract olgunlastirmasidir.
