# Hospital Backend

Bu proje, hastane yönetim sistemi için geliştirilen Spring Boot tabanlı backend uygulamasıdır.

Şu an proje **Faz 1** seviyesindedir. Bu fazda amaç, kimlik doğrulama olmadan çalışan çekirdek domain yapısını kurmak ve sonraki fazlarda genişletilecek mimari omurgayı sağlamlaştırmaktır.

## Faz 1 Kapsamı
- Bölüm yönetimi
- Doktor yönetimi
- Hasta yönetimi
- Randevu yönetimi
- Muayene kaydı
- Ödeme kaydı

## Kullanılan Teknolojiler
- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- MSSQL sürücü desteği
- Bean Validation

## Temel Mimari Yaklaşım
- Domain bazlı klasörleme kullanılır.
- Her domain içinde `controller`, `dto`, `mapper`, `model`, `repository`, `service` katmanları bulunur.
- İş kuralları controller içinde değil, service katmanında tutulur.
- Entity sınıfları dış dünyaya doğrudan açılmaz; DTO kullanılır.
- Dönüşümler manuel mapper sınıfları ile yapılır.
- Listeleme endpoint'lerinde pagination zorunludur.

## Faz 1 Domainleri
- `department`
- `doctor`
- `patient`
- `appointment`
- `encounter`
- `payment`
- `common`

## Veritabanı Kararları
- Tüm `id` alanları uygulama tarafında `UUID` olarak tutulur.
- Veritabanında `id` kolonları `varchar(36)` olarak saklanır.
- Zaman noktaları UTC mantığı ile yönetilir.
- Sadece tarih gereken alanlarda `LocalDate` kullanılır.
- Faz 1'de şema SQL dosyaları ile yönetilir.

## API Kararları
- Başarılı cevaplar `ApiResponse<T>` ile döner.
- Hata cevapları `ApiErrorResponse` ile döner.
- Liste endpoint'leri `PageResponse<T>` kullanır.
- Validation ve iş kuralı hataları ortak exception handler üzerinden yönetilir.

## SQL Yapısı
Faz 1 için SQL dosyaları şu klasörde tutulur:

`sql/phase-1-foundation/mysql`

Çalıştırma sırası:
- `01_departments.sql`
- `02_doctors.sql`
- `03_patients.sql`
- `04_appointments.sql`
- `05_encounters.sql`
- `06_payments.sql`

## Dokümantasyon
Detaylı açıklamalar `docs` klasörü altındadır:
- [Faz 1 Özeti](/Users/serdar/Documents/hospital/docs/phase-1-foundation.md)
- [Mimari Yaklaşım](/Users/serdar/Documents/hospital/docs/architecture.md)
- [Design Pattern ve Kavramlar](/Users/serdar/Documents/hospital/docs/design-patterns.md)
- [Proje Yapısı](/Users/serdar/Documents/hospital/docs/project-structure.md)
- [API Kuralları](/Users/serdar/Documents/hospital/docs/api-conventions.md)
- [Tarih ve Saat Kuralları](/Users/serdar/Documents/hospital/docs/date-time-conventions.md)

## Sonraki Fazlar
- Faz 2: auth, access token, refresh token, rol bazlı yetkilendirme
- Faz 3: modüler monolith yapısının genişletilmesi ve microservice'e hazır hale getirilmesi
