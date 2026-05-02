# Faz 3 Advanced MySQL Şeması

Bu klasörde ileri veritabanı dersi için hazırlanmış ek SQL scriptleri bulunur.

## Çalıştırma Sırası
- `01_doctor_workload_view.sql`
- `02_payment_audit_table.sql`
- `03_payment_audit_trigger.sql`
- `04_create_appointment_if_available.sql`

## İçerik Özeti
- `VIEW`: `doctor_workload_view`
- `TRIGGER`: `payments` tablosuna insert olduğunda `payment_audit` tablosuna log düşer
- `STORED PROCEDURE`: doktor için aynı tarih-saatte ikinci randevuyu engeller
- `CUSTOM SQL`: backend tarafında hasta bazlı ödeme özeti native query ile alınır

## Notlar
- Bu scriptler Faz 1 ve Faz 2 tabloları oluşturulduktan sonra çalıştırılmalıdır.
- Scriptler MySQL hedeflenerek yazılmıştır; H2 test veritabanında otomatik çalıştırılması beklenmez.
- Uygulama bu scriptler çalıştırılmadan da ayağa kalkar; ancak ilgili reporting ve procedure endpoint'leri anlamlı hata döndürür.
