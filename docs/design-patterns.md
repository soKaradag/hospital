# Design Pattern ve Kullanılan Kavramlar

Bu doküman projede geçen bazı tasarım yaklaşım ve kavramlarını yeni başlayan biri için açıklar.

## Service Layer Pattern
Service layer, controller ile repository arasındaki iş kuralı katmanıdır.

Neden kullanılır:
- iş kurallarını tek yerde toplamak
- controller'ı sade tutmak
- transaction yönetimini merkezileştirmek

Örnek:
- duplicate kontrolü
- ilişki doğrulaması
- tarih kontrolü

## Repository Pattern
Repository pattern, veritabanı erişimini soyutlamak için kullanılır.

Neden kullanılır:
- veri erişim kodunu tek yerde toplamak
- service katmanını SQL veya persistence detayından ayırmak
- test ve bakım kolaylığı sağlamak

## DTO Pattern
DTO, Data Transfer Object anlamına gelir.

Neden kullanılır:
- entity sınıflarını doğrudan dışarı açmamak
- API cevabını kontrol etmek
- request ve response modellerini açık biçimde ayırmak

## Mapper
Mapper, entity ile DTO arasında veri dönüştüren sınıftır.

Bu projede mapper'lar manuel yazılmıştır.

Neden manuel mapper:
- her alanın açıkça görülmesi
- dönüşümün daha kolay anlaşılması
- code generation bağımlılığını azaltmak

## Dependency Injection
Dependency Injection, bir sınıfın ihtiyaç duyduğu nesnelerin dışarıdan verilmesidir.

Örnek:
- repository
- mapper
- service

Neden kullanılır:
- sınıf kendi bağımlılığını üretmez
- test yazmak kolaylaşır
- bağlılık azalır

## Interface ve Impl Ayrımı
Örnek:
- `AppointmentService`
- `AppointmentServiceImpl`

Burada interface ne yapılacağını tanımlar, implementation ise nasıl yapılacağını yazar.

Faydaları:
- soyutlama sağlar
- test kolaylığı sağlar
- farklı implementasyonlara alan açar

## Global Exception Handler
Bu yapı, uygulamada fırlatılan hataları tek merkezden yakalamak için kullanılır.

Neden kullanılır:
- her controller içinde tekrar tekrar try-catch yazmamak
- tüm hata cevaplarını aynı formatta döndürmek
- HTTP status kodlarını merkezi yönetmek

## Value Object
`Address`, `Contact`, `Phone`, `Country`, `City` gibi sınıflar value object mantığıyla kullanılır.

Bunlar:
- ayrı tablo değildir
- kendi başına bir yaşam döngüsüne sahip değildir
- başka entity'lerin parçası olarak kullanılır

## Pagination
Bu projede liste endpoint'lerinde pagination zorunludur.

Neden kullanılır:
- büyük veri setlerinde performans sağlamak
- istemciye kontrollü veri vermek
- tek seferde aşırı kayıt dönmemek

## Transaction
`@Transactional`, bir metodun veritabanı işlemlerini tek bir transaction içinde çalıştırır.

Neden kullanılır:
- işlemler ya tamamen başarılı olur ya tamamen geri alınır
- veri tutarlılığı korunur

## Manual SQL + JPA Model Uyumu
Faz 1'de şema SQL dosyaları ile oluşturulur, ancak Java tarafında entity modelleri de vardır.

Bu yaklaşımın anlamı:
- SQL veritabanı gerçeğini tanımlar
- JPA entity ise uygulama modelini tanımlar
- iki tarafın birebir uyumlu olması gerekir
