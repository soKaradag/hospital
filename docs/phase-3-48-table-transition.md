# Faz 3 - 48 Tabloya Geçiş Planı

Bu doküman, backend yapısını mevcut `16` tablodan kontrollü biçimde `48` tabloya genişletmek için hazırlanmıştır.

Amaç, projeyi anlamsız biçimde şişirmek değildir. Her yeni tablo, var olan domain akışlarını daha doğru modellemek, UI geliştirmesini kolaylaştırmak ve reporting senaryolarını güçlendirmek için eklenir.

## Hedef

- Mevcut yapı: `16` tablo
- Hedef yapı: `48` tablo
- Eklenecek yeni tablo sayısı: `32`

## Uygulama Durumu

- Hedef tablo sayisina ulasildi: `48`
- Permission-first RBAC aktif
- Flyway migration zinciri `V1` - `V17` araliginda tamamlandi
- Kalan is, cleanup ve son contract sabitlemesidir

## Temel Kurallar

- Genişleme big-bang şeklinde yapılmayacaktır.
- Her yeni tablo doğrudan bir iş ihtiyacını karşılamalıdır.
- Mevcut Faz 1 ve Faz 2 CRUD akışları korunacaktır.
- Domain bazlı klasörleme bozulmayacaktır.
- Ortak yardımcı yapılar için gereksiz tablo üretilmeyecektir.
- `common` alanı tablo değil, ortak model ve yardımcı katman olarak kalacaktır.
- Lookup tabloya dönüşecek alanlar seçici biçimde ele alınacaktır; her enum tabloya dönüştürülmeyecektir.

## Yetkilendirme Kuralı

Auth alanı Faz 3 ile birlikte enum tabanlı rol yaklaşımından tablo tabanlı RBAC yapısına evrilir.

Kurallar:
- Bir kullanıcı birden fazla role sahip olabilir.
- Kullanıcının efektif izin kümesi, sahip olduğu tüm rollerin izinlerinin birleşimidir.
- Aynı izin iki farklı rolden gelirse çakışma oluşturmaz.
- İlk sürümde `deny` veya `override` mantığı yoktur; sadece `grant` mantığı vardır.

Veri bütünlüğü için:
- `role_permissions` tablosunda `unique(role_id, permission_id)` olmalıdır.
- `user_roles` tablosunda `unique(user_id, role_id)` olmalıdır.

Bu yaklaşım, çakışmasız izin toplama mantığı sağlar ve auth katmanını gereksiz karmaşıklığa götürmez.

## Domain Bazlı Hedef Tablo Haritası

### Auth

Mevcut:
- `users`
- `user_info`
- `refresh_tokens`

Yeni:
- `roles`
- `permissions`
- `role_permissions`
- `user_roles`
- `login_attempts`
- `password_reset_tokens`

Hedef toplam: `9`

Not:
- Endpoint yetkilendirme yapisi permission-first modele tasinmistir.
- `users.role` gecis notu olarak kalmistir; son cleanup adiminda tamamen degerlendirilecektir.

### Audit

Mevcut:
- `audit_logs`

Yeni:
- `audit_log_details`

Hedef toplam: `2`

Not:
- Ana audit kaydı korunur.
- Değişiklik detayları veya ek hata bağlamı ayrı tabloda tutulabilir.

### Department

Mevcut:
- `departments`

Yeni:
- `rooms`
- `department_service_catalog`

Hedef toplam: `3`

Not:
- Bölümün yalnızca isim listesi değil, fiziksel ve operasyonel kapasitesi de modellenmiş olur.

### Doctor

Mevcut:
- `doctors`

Yeni:
- `specialties`
- `doctor_specialties`

Hedef toplam: `3`

Not:
- `specialization` string alanı kademeli olarak normalize edilir.
- Tek doktorun birden fazla uzmanlığı olabilmesi desteklenir.

### Doctor Schedule

Mevcut:
- `doctor_schedules`

Yeni:
- `doctor_leaves`
- `doctor_schedule_exceptions`

Hedef toplam: `3`

Not:
- Haftalık tekrar eden plan korunur.
- İzin, resmi tatil, tekil müsaitlik değişikliği gibi istisnalar ayrı tutulur.

### Patient

Mevcut:
- `patients`

Yeni:
- `patient_emergency_contacts`
- `patient_insurances`

Hedef toplam: `3`

Not:
- Hasta kaydı yalnızca temel kimlik bilgisi olmaktan çıkar.
- İletişim ve finansal/kurumsal hasta bağlamı güçlenir.

### Disease

Mevcut:
- `diseases`

Yeni:
- `disease_categories`
- `disease_code_mappings`

Hedef toplam: `3`

Not:
- Hastalık kataloğu sınıflandırılabilir hale gelir.
- ICD benzeri dış kod sistemlerine bağ kurma zemini oluşur.

### Patient Disease

Mevcut:
- `patient_diseases`

Yeni:
- `patient_disease_status_history`
- `patient_disease_followups`

Hedef toplam: `3`

Not:
- Hastanın hastalık kaydı artık yalnızca tek satırlık geçmiş değil, takip edilebilir klinik durum olur.

### Appointment

Mevcut:
- `appointments`

Yeni:
- `appointment_status_history`
- `appointment_reminders`

Hedef toplam: `3`

Not:
- Randevu durum geçişleri izlenebilir olur.
- Bildirim ve hatırlatma akışları için altyapı hazırlanır.

### Encounter

Mevcut:
- `encounters`

Yeni:
- `encounter_vitals`
- `encounter_procedures`

Hedef toplam: `3`

Not:
- Encounter üst kaydı korunur.
- Vital bulgular ve uygulanan işlemler ayrı klinik nesnelere ayrılır.

### Encounter Diagnosis

Mevcut:
- `encounter_diagnoses`

Yeni:
- `encounter_diagnosis_history`

Hedef toplam: `2`

Not:
- Teşhis değişimi, güncellemesi veya revizyon izi takip edilebilir hale gelir.

### Prescription

Mevcut:
- `prescriptions`

Yeni:
- `medications`
- `prescription_items`
- `prescription_dispenses`

Hedef toplam: `4`

Not:
- Reçete üst kaydı korunur.
- İlaç kataloğu, reçete satırları ve teslim/karşılama bilgisi ayrıştırılır.

### Payment

Mevcut:
- `payments`

Yeni:
- `invoices`
- `payment_transactions`
- `payment_refunds`

Hedef toplam: `4`

Not:
- Ödeme kaydı ile tahsilat hareketi aynı şey olmaktan çıkar.
- Fatura ve iade akışları ayrı izlenir.

### Reporting

Mevcut:
- `payment_audit`

Yeni:
- `report_snapshots`
- `report_export_jobs`

Hedef toplam: `3`

Not:
- Sadece anlık sorgu değil, saklanan rapor çıktıları ve dışa aktarma işleri de modellenir.

## Toplam Hesap

- Auth: `9`
- Audit: `2`
- Department: `3`
- Doctor: `3`
- Doctor Schedule: `3`
- Patient: `3`
- Disease: `3`
- Patient Disease: `3`
- Appointment: `3`
- Encounter: `3`
- Encounter Diagnosis: `2`
- Prescription: `4`
- Payment: `4`
- Reporting: `3`

Genel toplam: `48`

## Geçiş Sırası

### Faz 3A - Auth ve Erişim Modeli

Amaç:
- Rol enum yapısını tablo tabanlı RBAC modeline taşımak
- Çoklu rol desteğini eklemek
- Çakışmasız izin birleşimini garanti etmek

Tablolar:
- `roles`
- `permissions`
- `role_permissions`
- `user_roles`
- `login_attempts`
- `password_reset_tokens`

### Faz 3B - Ana Veri ve Organizasyon Genişlemesi

Amaç:
- String veya tek alanla taşınan master data alanlarını normalize etmek
- Klinik operasyonu daha gerçekçi hale getirmek

Tablolar:
- `specialties`
- `doctor_specialties`
- `rooms`
- `department_service_catalog`
- `disease_categories`
- `disease_code_mappings`

### Faz 3C - Planlama ve Hasta Bağlamı

Amaç:
- Doktor takvimi, randevu ve hasta bağlamını daha gerçekçi hale getirmek

Tablolar:
- `doctor_leaves`
- `doctor_schedule_exceptions`
- `patient_emergency_contacts`
- `patient_insurances`
- `appointment_status_history`
- `appointment_reminders`

### Faz 3D - Klinik Derinleşme

Amaç:
- Encounter sonrası oluşan klinik detayları üst kayıttan ayırmak

Tablolar:
- `patient_disease_status_history`
- `patient_disease_followups`
- `encounter_vitals`
- `encounter_procedures`
- `encounter_diagnosis_history`
- `medications`
- `prescription_items`
- `prescription_dispenses`

### Faz 3E - Finans ve Reporting Olgunlaştırma

Amaç:
- Ödeme ve raporlama yapısını CRUD seviyesinden süreç seviyesine taşımak

Tablolar:
- `audit_log_details`
- `invoices`
- `payment_transactions`
- `payment_refunds`
- `report_snapshots`
- `report_export_jobs`

## API ve Uygulama Uyumluluğu

Geçiş sırasında şu yaklaşım korunacaktır:
- Mevcut endpoint isimleri mümkün olduğunca korunur.
- Yeni tablolar ilk aşamada mevcut akışların arkasına eklenir.
- Gerekmedikçe mevcut response şemaları kırılmaz.
- String veya enum alanlar lookup tabloya taşınsa bile API cevapları bir süre sade tutulabilir.
- UI geliştirmesi ile backend şema geçişi birbirini bloklamayacak şekilde ilerletilir.

## Bu Fazda Bilinçli Olarak Yapılmayacaklar

- `Spring Security` geçişi
- Kullanıcı bazlı özel izin override modeli
- `deny` tabanlı karmaşık yetki çatışma sistemi
- Her enum için ayrı tablo üretme
- Tek bir generic `documents`, `metadata` veya `settings` tablosu ile her şeyi çözmeye çalışma

## Sonuç

Bu planın amacı tablo sayısını sadece büyütmek değil, mevcut projeyi daha gerçekçi bir hastane operasyon omurgasına dönüştürmektir.

`16 -> 48` gecisi uygulama tarafinda tamamlanmistir. Bu gecis ancak asagidaki uc kosul korunursa saglikli kabul edilir:
- her tablo net bir iş anlamı taşımalı
- mevcut domain sınırları bozulmamalı
- auth, klinik kayıt ve finans akışları birlikte olgunlaşmalıdır
