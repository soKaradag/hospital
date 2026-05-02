# Hospital Backend

Bu proje, hastane yonetim sistemi icin gelistirilen Spring Boot tabanli backend uygulamasidir.

Su an proje **Phase 3 foundation** seviyesindedir. Faz 1 ve Faz 2 cekirdek akislarinin uzerine permission-first RBAC, Flyway tabanli schema yonetimi ve domain derinlestirme calismalari eklenmistir.

## Mevcut Durum
- Schema kaynagi Flyway migration dosyalaridir.
- Yetkilendirme modeli permission-first RBAC yapisina tasinmistir.
- `/api/auth/me` `roles` ve `permissions` doner.
- Backend tablo sayisi su anda `48` seviyesindedir.

## Kapsam
- Custom authentication altyapisi (`Spring Security` framework olarak kullanilmaz)
- JWT access token ve refresh token yapisi
- `login`, `refresh`, `logout`, `me` auth akisleri
- `@RequirePermission` ile permission tabanli yetkilendirme
- `@Audit` + observer pattern ile audit event ve DB persistence yapisi
- Klinik, operasyonel, finansal ve reporting domain genislemeleri

## Kullanilan Teknolojiler
- Java 21
- Spring Boot
- Spring Data JPA
- Flyway
- MySQL
- MSSQL surucu destegi
- Bean Validation
- Spring AOP
- `spring-security-crypto` (`BCrypt` icin)

## Temel Mimari Yaklasim
- Domain bazli klasorleme kullanilir.
- Her domain icinde `controller`, `dto`, `mapper`, `model`, `repository`, `service` katmanlari bulunur.
- Is kurallari controller icinde degil, service katmaninda tutulur.
- Entity siniflari dis dunyaya dogrudan acilmaz; DTO kullanilir.
- Donusumler manuel mapper siniflari ile yapilir.
- Listeleme endpoint'lerinde pagination zorunludur.
- Auth ve audit cross-cutting concern olarak merkezi katmanlarda cozulur.

## Guvenlik Kararlari
- Auth yapisi custom tasarlanmistir.
- Roller veri kaydidir; erisim karari dogrudan rol isminden verilmez.
- Yetki kontrolu `@RequirePermission` ve merkezi interceptor akisi ile uygulanir.
- Kullanici birden fazla role sahip olabilir.
- Efektif permission kumesi, rollerden gelen izinlerin cakismasiz birlesimidir.
- Sifreler `BCrypt` ile hash'lenir.
- Refresh token DB uzerinden yonetilir ve hash olarak saklanir.

## Veritabani Kararlari
- Tum `id` alanlari uygulama tarafinda `UUID` olarak tutulur.
- Veritabaninda `id` kolonlari `varchar(36)` olarak saklanir.
- Zaman noktalari UTC mantigi ile yonetilir.
- Sadece tarih gereken alanlarda `LocalDate` kullanilir.
- Sadece saat gereken alanlarda `LocalTime` kullanilir.
- Schema migrationlari `src/main/resources/db/migration` altinda tutulur.

## Tablo Durumu
Toplam tablo sayisi su anda `48`:

- Cekirdek: `departments`, `doctors`, `patients`, `appointments`, `encounters`, `payments`
- Auth/RBAC: `users`, `user_info`, `refresh_tokens`, `roles`, `permissions`, `role_permissions`, `user_roles`, `login_attempts`, `password_reset_tokens`
- Audit: `audit_logs`, `audit_log_details`
- Doctor ve department genislemeleri: `doctor_schedules`, `specialties`, `doctor_specialties`, `rooms`, `department_service_catalog`, `doctor_leaves`, `doctor_schedule_exceptions`
- Clinical catalog: `diseases`, `disease_categories`, `disease_code_mappings`
- Patient tracking: `patient_diseases`, `patient_disease_status_history`, `patient_disease_followups`, `patient_emergency_contacts`, `patient_insurances`
- Appointment ve encounter genislemeleri: `appointment_status_history`, `appointment_reminders`, `encounter_vitals`, `encounter_procedures`, `encounter_diagnoses`, `encounter_diagnosis_history`
- Prescription genislemeleri: `prescriptions`, `medications`, `prescription_items`, `prescription_dispenses`
- Finance ve reporting: `invoices`, `payment_transactions`, `payment_refunds`, `payment_audit`, `report_snapshots`, `report_export_jobs`

## API Kararlari
- Basarili cevaplar `ApiResponse<T>` ile doner.
- Hata cevaplari `ApiErrorResponse` ile doner.
- Liste endpoint'leri `PageResponse<T>` kullanir.
- Validation ve is kurali hatalari ortak exception handler uzerinden yonetilir.
- Korumali endpoint'lerde `Authorization: Bearer <accessToken>` kullanilir.

## Dokumantasyon
Detayli aciklamalar `docs` klasoru altindadir:
- [Faz 1 Ozeti](docs/phase-1-foundation.md)
- [Faz 2 Gelistirme Adimlari](docs/phase-2-implementation-steps.md)
- [Mimari Yaklasim](docs/architecture.md)
- [Design Pattern ve Kavramlar](docs/design-patterns.md)
- [Proje Yapisi](docs/project-structure.md)
- [API Kurallari](docs/api-conventions.md)
- [Faz 2 API Contract](docs/api-contract-phase-2.md)
- [Tarih ve Saat Kurallari](docs/date-time-conventions.md)
- [Faz 3 - 48 Tabloya Gecis Plani](docs/phase-3-48-table-transition.md)
- [Faz 3 - Uygulama Yurutme Plani](docs/phase-3-execution-plan.md)
- [Faz 4 - Inventory Service Tasarimi](docs/phase-4-inventory-service-design.md)

## Sonraki Yon
- Kalan ana is cleanup ve contract sabitlemedir.
- Gecis notlari, dokuman uyarlamasi ve varsa bridge temizligi ayri bir kapanis adiminda toparlanacaktir.
