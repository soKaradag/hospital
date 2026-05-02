# Common Domain

Bu alan, tüm proje tarafından kullanılacak ortak yapılar için ayrılmıştır.

## Amaç
- Base entity yapıları
- Ortak value object modelleri
- Ortak exception sınıfları
- Ortak response modelleri
- Yardımcı sınıflar
- Ortak enum ve sabitler

## Faz 1 Ortak API Yapısı
- Tüm başarılı cevaplar ortak response modeli ile döner.
- Tüm hata cevapları ortak error modeli ile döner.
- Hata kodları merkezi bir enum üzerinden yönetilir.
- Validation hataları ortak bir detail listesi ile taşınır.
- `Address` ve `Contact` gibi tekrar kullanılabilir modeller ortak alanda tutulur.
- `Phone`, `Country` ve `City` gibi alt değer nesneleri de ortak alanda tutulur.
