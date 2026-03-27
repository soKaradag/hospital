# Faz 2 MySQL Şeması

Bu klasörde Faz 2 için eklenecek MySQL tablo scriptleri bulunur.

## Çalıştırma Sırası
- `01_auth_users.sql`
- `02_refresh_tokens.sql`
- `03_audit_logs.sql`
- `04_doctor_schedules.sql`

## Notlar
- Tüm `id` alanları `varchar(36)` olarak tutulur.
- Tüm zaman noktaları UTC kabul edilerek `datetime(6)` olarak tutulur.
- Faz 2 scriptleri Faz 1 tablolarının üstüne eklenmek üzere tasarlanır.
