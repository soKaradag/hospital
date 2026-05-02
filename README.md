# Hospital Backend

Bu proje, hastane yonetim sistemi icin gelistirilen Spring Boot tabanli backend uygulamasidir.

Su an proje **Phase 4 foundation** seviyesindedir. Faz 3'te kurulan permission-first RBAC ve domain derinlestirme omurgasinin yanina, ayni repo icinde calisan `inventory-service` ve cerrahi hazirlik akislarinin operasyonel kapanisi eklenmistir.

## Mevcut Durum
- Schema kaynagi Flyway migration dosyalaridir.
- Yetkilendirme modeli permission-first RBAC yapisina tasinmistir.
- `/api/auth/me` `roles` ve `permissions` doner.
- `hospital-core` tablo sayisi su anda `56` seviyesindedir.
- `inventory-service` tablo sayisi su anda `22` seviyesindedir.
- Ekosistem toplam tablo sayisi su anda `78` seviyesindedir.

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
`hospital-core` tablo sayisi su anda `56`:

- Cekirdek: `departments`, `doctors`, `patients`, `appointments`, `encounters`, `payments`
- Auth/RBAC: `users`, `user_info`, `refresh_tokens`, `roles`, `permissions`, `role_permissions`, `user_roles`, `login_attempts`, `password_reset_tokens`
- Audit: `audit_logs`, `audit_log_details`
- Doctor ve department genislemeleri: `doctor_schedules`, `specialties`, `doctor_specialties`, `rooms`, `department_service_catalog`, `doctor_leaves`, `doctor_schedule_exceptions`
- Clinical catalog: `diseases`, `disease_categories`, `disease_code_mappings`
- Patient tracking: `patient_diseases`, `patient_disease_status_history`, `patient_disease_followups`, `patient_emergency_contacts`, `patient_insurances`
- Appointment ve encounter genislemeleri: `appointment_status_history`, `appointment_reminders`, `encounter_vitals`, `encounter_procedures`, `encounter_diagnoses`, `encounter_diagnosis_history`
- Prescription genislemeleri: `prescriptions`, `medications`, `prescription_items`, `prescription_dispenses`
- Finance ve reporting: `invoices`, `payment_transactions`, `payment_refunds`, `payment_audit`, `report_snapshots`, `report_export_jobs`
- Surgery genislemeleri: `operating_rooms`, `surgery_requests`, `surgeries`, `surgery_team_assignments`, `surgery_status_history`, `doctor_procedure_privileges`, `surgery_supply_templates`, `surgery_supply_template_items`

`inventory-service` tablo sayisi su anda `22`:

- Master data: `warehouses`, `warehouse_zones`, `inventory_categories`, `inventory_items`, `inventory_item_units`, `inventory_item_aliases`, `inventory_item_barcodes`, `suppliers`
- Stock operations: `stock_batches`, `stock_movements`, `stock_reservations`, `stock_adjustments`, `stock_transfer_requests`, `stock_transfers`, `stock_counts`, `stock_count_lines`
- Procurement ve planning: `purchase_orders`, `purchase_order_items`, `goods_receipts`, `goods_receipt_items`, `supplier_catalog_items`, `reorder_rules`

## Faz 4 Local Calistirma

Varsayilan local sozlesme:
- `hospital-core`: `http://127.0.0.1:8080`
- `inventory-service`: `http://127.0.0.1:8081`
- Core DB: `hospital`
- Inventory DB: `hospital_inventory`
- Varsayilan admin kullanicisi: `admin / admin123`

Temel komutlar:
- DB reset: `bash scripts/phase4-db-reset.sh`
- Iki servisi birlikte baslat: `bash scripts/phase4-up.sh`
- Uctan uca Faz 4 smoke: `bash scripts/phase4-smoke.sh`

Beklenen smoke sonucu:
- inventory standalone akisi gecer: category -> warehouse -> supplier -> item -> purchase order -> goods receipt -> availability
- clinical akisi gecer: encounter consultation consume -> prescription consume -> surgery reserve/cancel/complete
- script cikisinda `Phase 4 dual-service smoke passed.` gorulur

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
- [Faz 4 - Inventory Service Yurutme Plani](docs/phase-4-execution-plan.md)

## Sonraki Yon
- Faz 4 foundation artik iki servisli local runtime ve smoke ile dogrulanmis durumdadir.
- Sonraki ana adaylar production deployment profili, CI smoke otomasyonu ve gerekli gorulurse inventory event/outbox olgunlastirmasidir.
