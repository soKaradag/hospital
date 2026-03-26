# Faz 1 MySQL Şeması

Bu klasörde Faz 1 için MySQL tablo scriptleri bulunur.

## Çalıştırma Sırası
- `01_departments.sql`
- `02_doctors.sql`
- `03_patients.sql`
- `04_appointments.sql`
- `05_encounters.sql`
- `06_payments.sql`

## Notlar
- Tüm `id` alanları `varchar(36)` olarak tutulur.
- Tüm zaman noktaları UTC kabul edilerek `datetime(6)` olarak tutulur.
- `birth_date` gibi sadece tarih gereken alanlar `date` olarak tutulur.
- `Address`, `Contact`, `Phone`, `Country` ve `City` ayrı tablo değildir; ilgili tabloların kolonlarına gömülür.
