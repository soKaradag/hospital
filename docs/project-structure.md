# Proje Yapısı

Bu doküman klasör yapısının ne anlama geldiğini açıklar.

## Java Paket Yapısı

Kök paket:
- `com.hospital.hospital`

Ana domainler:
- `common`
- `department`
- `doctor`
- `patient`
- `appointment`
- `encounter`
- `payment`

## Her Domain İçindeki Katmanlar

### model
Entity ve domain model sınıfları burada bulunur.

### repository
Spring Data JPA repository arayüzleri burada bulunur.

### dto
Request ve response modelleri burada bulunur.

### mapper
Entity ile DTO dönüşümlerini yapan manuel mapper sınıfları burada bulunur.

### service
İş kuralları ve uygulama akışları burada bulunur.

### controller
HTTP endpoint'leri burada bulunur.

## Common Klasörü
`common` alanı gerçekten ortak olan yapılar için ayrılmıştır.

Örnek:
- ortak DTO'lar
- ortak exception sınıfları
- ortak value object'ler
- ortak mapper yardımcıları

## Docs Klasörü
`docs` klasörü, proje ile ilgili yazılı teknik kararları içerir.

Burada:
- faz özetleri
- mimari yaklaşım
- API kuralları
- tarih/saat kuralları
- pattern açıklamaları
bulunur.

## SQL Klasörü
SQL dosyaları uygulamadan ayrı tutulur.

Klasör:
- `sql/phase-1-foundation/mysql`

Bu yapı sayesinde:
- veritabanı scriptleri açık şekilde görülebilir
- çalıştırma sırası korunabilir
- Faz 1 ile sonraki fazların şemaları ayrıştırılabilir
