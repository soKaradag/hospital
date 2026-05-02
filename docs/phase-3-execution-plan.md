# Faz 3 - Uygulama Yurutme Plani

Bu dokuman, `16` tablodan `48` tabloya gecis hedefini kod tarafinda en verimli sekilde uygulamak icin hazirlanmistir.

## Guncel Durum

- Hedef tablo sayisina ulasildi: `48`
- Kod tarafinda planlanan tablo genisletme adimlari tamamlandi
- Kalan isler cleanup, bridge degerlendirmesi, README ve final contract kapatisidir

Plan, uc ana bolume ayrilir. Her bolum alt bolumlere, her alt bolum de calisan ve commitlenebilir adimlara bolunmustur.

## Uygulama Kurallari

- Her adim sonunda proje derlenebilir durumda kalmalidir.
- Her adim sonunda minimum ilgili test veya compile komutu calistirilmalidir.
- Her adim tamamlandiginda tek amacli bir commit atilmalidir.
- Bir adim birden fazla hedefe dagilmamalidir.
- Riskli migration adimlari dogrudan kirici gecisle degil, kopru stratejisiyle uygulanmalidir.

## Bolum 1 - Erisim ve Referans Veri Altyapisi

Bu bolumun amaci auth omurgasini enum tabanli rolden tablo tabanli RBAC yapisina tasimak ve buyuyecek referans verileri normalize etmektir.

### 1.1 RBAC Omurgasi

#### Adim 1 - RBAC cekirdek tablolari

Kapsam:
- `roles`
- `permissions`
- `role_permissions`
- `user_roles`
- seed verileri
- benzersizlik ve foreign key kurallari

Teslim:
- SQL scriptleri
- entity ve repository iskeletleri
- ilk seed stratejisi

Commit basligi:
`Add RBAC core schema and seed data`

Commit mesaji:
`Introduce roles, permissions, role_permissions and user_roles tables with initial seed data to prepare the backend for multi-role authorization.`

#### Adim 2 - RBAC kopru gecisi

Kapsam:
- mevcut auth akisini kirmadan yeni rol tablolarini okumaya hazirlamak
- `users.role` ile yeni rol iliskileri bir sure birlikte yasayacak sekilde gecis katmani kurmak
- effective permission hesaplama servisi

Teslim:
- gecis servisi
- user-role resolution
- permission union mantigi

Commit basligi:
`Bridge enum role auth to relational RBAC`

Commit mesaji:
`Add a transition layer that resolves user roles and effective permissions from relational RBAC while keeping the current auth flow stable during migration.`

#### Adim 3 - Permission tabanli endpoint korumasi

Kapsam:
- `@RequireRole` yapisina paralel permission tabanli annotation eklemek
- kritik endpointlerde permission kontrolunu devreye almak
- duplicate permission durumlarinda cakismaz union kuralini test etmek

Teslim:
- yeni annotation
- authorization service guncellemesi
- testler

Commit basligi:
`Introduce permission-based authorization guards`

Commit mesaji:
`Add permission-driven endpoint protection and verify that duplicate permissions coming from multiple roles are resolved as a non-conflicting union.`

### 1.2 Auth Guclendirme

#### Adim 4 - Auth operasyon tablolari

Kapsam:
- `login_attempts`
- `password_reset_tokens`
- login guvenligi ve sifre sifirlama akislarina temel olusturma

Teslim:
- SQL scriptleri
- entity, repository, service katmanlari
- temel API ve is kurali testleri

Commit basligi:
`Add auth security support tables`

Commit mesaji:
`Introduce login_attempts and password_reset_tokens to support authentication hardening and future password recovery flows.`

### 1.3 Referans Veri Normalizasyonu

#### Adim 5 - Doktor uzmanlik yapisi

Kapsam:
- `specialties`
- `doctor_specialties`
- mevcut `doctor.specialization` string alanini kopru modda koruma

Teslim:
- SQL scriptleri
- entity iliskileri
- doktor create/update/list akislarinin uyarlanmasi

Commit basligi:
`Normalize doctor specialties`

Commit mesaji:
`Add specialties and doctor_specialties tables and prepare doctor flows for normalized multi-specialty support.`

#### Adim 6 - Bolum operasyon verisi

Kapsam:
- `rooms`
- `department_service_catalog`
- bolumun fiziksel ve operasyonel tanimini genisletme

Teslim:
- SQL scriptleri
- domain modelleri
- temel listeleme ve CRUD akislarinin ilk surumu

Commit basligi:
`Expand department operational data`

Commit mesaji:
`Introduce rooms and department_service_catalog to model department capacity and service structure beyond basic department records.`

#### Adim 7 - Hastalik katalogu genislemesi

Kapsam:
- `disease_categories`
- `disease_code_mappings`
- disease katalogunu dis kod sistemleriyle esleyebilir hale getirme

Teslim:
- SQL scriptleri
- entity ve service katmanlari
- disease CRUD ve filtreleme uyarlamasi

Commit basligi:
`Extend disease catalog structure`

Commit mesaji:
`Add disease_categories and disease_code_mappings to classify diseases and prepare the catalog for external coding standards.`

## Bolum 2 - Operasyonel ve Klinik Derinlesme

Bu bolumun amaci hasta, doktor, randevu ve klinik kayit akislarini gercek hastane operasyonuna daha yakin hale getirmektir.

### 2.1 Doktor ve Hasta Baglami

#### Adim 8 - Doktor takvim istisnalari

Kapsam:
- `doctor_leaves`
- `doctor_schedule_exceptions`
- haftalik plan ile tarih bazli istisnalari ayirma

Teslim:
- SQL scriptleri
- service kurallari
- schedule cakismazlik kontrolleri

Commit basligi:
`Add doctor leave and schedule exception support`

Commit mesaji:
`Introduce doctor_leaves and doctor_schedule_exceptions to model date-based schedule overrides without breaking recurring weekly schedules.`

#### Adim 9 - Hasta baglamsal bilgiler

Kapsam:
- `patient_emergency_contacts`
- `patient_insurances`
- hasta kaydini operasyonel olarak derinlestirme

Teslim:
- SQL scriptleri
- patient domain genislemesi
- create/update/detail akislarinin uyarlanmasi

Commit basligi:
`Extend patient contextual records`

Commit mesaji:
`Add patient_emergency_contacts and patient_insurances to enrich patient records with operational and billing context.`

### 2.2 Randevu ve Takip Akislari

#### Adim 10 - Randevu durum gecmisi

Kapsam:
- `appointment_status_history`
- durum degisikliklerinin izlenebilir hale gelmesi

Teslim:
- SQL scriptleri
- status transition servis mantigi
- audit benzeri gecmis sorgulama destegi

Commit basligi:
`Track appointment status transitions`

Commit mesaji:
`Introduce appointment_status_history so appointment lifecycle changes become explicit, queryable and auditable.`

#### Adim 11 - Randevu hatirlatma yapisi

Kapsam:
- `appointment_reminders`
- gelecekte bildirim gonderimi icin veri zemini

Teslim:
- SQL scriptleri
- reminder olusturma mantigi
- status ile uyumlu kurallar

Commit basligi:
`Add appointment reminder scheduling data`

Commit mesaji:
`Add appointment_reminders to prepare the system for reminder scheduling and notification-oriented appointment workflows.`

#### Adim 12 - Hasta hastalik takibi

Kapsam:
- `patient_disease_status_history`
- `patient_disease_followups`
- hastalik kaydini surec bazli izleme

Teslim:
- SQL scriptleri
- service genislemesi
- takip ve durum degisimi testleri

Commit basligi:
`Deepen patient disease follow-up model`

Commit mesaji:
`Introduce patient_disease_status_history and patient_disease_followups to convert patient disease records into a trackable longitudinal workflow.`

### 2.3 Klinik Kayit Derinlesmesi

#### Adim 13 - Encounter vital ve prosedurleri

Kapsam:
- `encounter_vitals`
- `encounter_procedures`
- encounter ust kaydini klinik alt kayitlarla genisletme

Teslim:
- SQL scriptleri
- encounter servis uyarlamasi
- klinik veri mapleme guncellemeleri

Commit basligi:
`Add encounter vitals and procedures`

Commit mesaji:
`Introduce encounter_vitals and encounter_procedures to separate clinical measurements and applied procedures from the encounter header record.`

#### Adim 14 - Teshis revizyon gecmisi

Kapsam:
- `encounter_diagnosis_history`
- encounter diagnosis guncellemelerinde iz birakma

Teslim:
- SQL scriptleri
- diagnosis update akislarinin uyarlanmasi
- tarihsel kayit testleri

Commit basligi:
`Track encounter diagnosis revisions`

Commit mesaji:
`Add encounter_diagnosis_history so diagnosis changes can be reviewed as a formal revision trail instead of overwriting prior state.`

#### Adim 15 - Recete satir yapisi

Kapsam:
- `medications`
- `prescription_items`
- `prescription_dispenses`
- prescription ust kaydini gercek ilac akisina tasima

Teslim:
- SQL scriptleri
- entity iliskileri
- recete detay endpointleri

Commit basligi:
`Expand prescriptions into medication line items`

Commit mesaji:
`Introduce medications, prescription_items and prescription_dispenses to evolve prescriptions from header-only records into detailed medication workflows.`

## Bolum 3 - Finans, Raporlama ve Kapanis

Bu bolumun amaci finans akisini olgunlastirmak, raporlamayi kalici hale getirmek ve gecisi dokumantasyon ile stabilizasyon tarafinda kapatmaktir.

### 3.1 Audit ve Finans Derinlesmesi

#### Adim 16 - Audit detay tablosu

Kapsam:
- `audit_log_details`
- audit kaydina alan bazli veya baglamsal detay ekleme

Teslim:
- SQL scriptleri
- audit observer guncellemesi
- hata ve degisiklik detayi saklama destegi

Commit basligi:
`Add audit log detail records`

Commit mesaji:
`Introduce audit_log_details to capture structured contextual information alongside top-level audit log entries.`

#### Adim 17 - Fatura ve odeme hareketleri

Kapsam:
- `invoices`
- `payment_transactions`
- `payment_refunds`
- odeme kaydi ile finans hareketini ayirma

Teslim:
- SQL scriptleri
- payment servis genislemesi
- payment/reporting sorgu uyarlamalari

Commit basligi:
`Mature payment flows with invoices and transactions`

Commit mesaji:
`Add invoices, payment_transactions and payment_refunds to separate billing, settlement and refund workflows from the base payment record.`

### 3.2 Reporting Kaliciligi

#### Adim 18 - Rapor saklama ve disa aktarma isleri

Kapsam:
- `report_snapshots`
- `report_export_jobs`
- reporting sonucunu yalnizca anlik sorgu olmaktan cikarma

Teslim:
- SQL scriptleri
- reporting servis uyarlamasi
- export job status modeli

Commit basligi:
`Persist reporting snapshots and export jobs`

Commit mesaji:
`Introduce report_snapshots and report_export_jobs so reporting can support stored outputs and asynchronous export-oriented workflows.`

### 3.3 Kapanis ve Gecis Temizligi

#### Adim 19 - Enum temizligi ve lookup gecislerinin tamamlanmasi

Kapsam:
- tablolasmis alanlar icin artik gereksiz kalan enum veya gecis kodunu temizleme
- `users.role` gibi gecici alanlarin kaldirilmasi
- eski ve yeni veri modelinin tek sisteme dusurulmesi

Teslim:
- final migration scriptleri
- kod sadeleştirmesi
- backward-compatibility kararlarinin netlestirilmesi

Commit basligi:
`Finalize lookup migrations and remove transition paths`

Commit mesaji:
`Remove temporary migration bridges and finalize the transition from enum-based fields to relational lookup and RBAC structures.`

#### Adim 20 - API contract, test ve dokuman kapanisi

Kapsam:
- Faz 3 API contract guncellemeleri
- README ve domain README guncellemeleri
- minimum smoke test ve compile dogrulamasi

Teslim:
- guncel dokumanlar
- test raporu
- final tablo sayisi dogrulamasi

Commit basligi:
`Document and verify the phase 3 expansion`

Commit mesaji:
`Update contracts, documentation and verification coverage to close the phase 3 expansion with a consistent 48-table backend model.`

## Onerilen Uygulama Sirasi Ozeti

1. Once auth gecisi ve RBAC kopru katmani tamamlanir.
2. Sonra buyuyecek referans veriler normalize edilir.
3. Ardindan doktor, hasta, randevu ve klinik akislar derinlestirilir.
4. En sonda finans, reporting ve migration temizligi kapatilir.

Bu sira, sistemi en az kiran ve her adim sonunda commit atilabilir durumda tutan en dengeli akistir.
