# Faz 1 Özeti

Bu doküman, projenin ilk fazında nelerin yapıldığını ve neden bu kapsamın seçildiğini açıklar.

## Faz 1 Amacı
- Kimlik doğrulama olmadan çalışan çekirdek backend yapısını kurmak
- Temel domain ilişkilerini oturtmak
- Sonraki fazlarda bozulmadan genişleyebilecek omurgayı oluşturmak

## Faz 1 İçindeki Domainler
- `department`
- `doctor`
- `patient`
- `appointment`
- `encounter`
- `payment`
- `common`

## Faz 1 Tabloları
- `departments`
- `doctors`
- `patients`
- `appointments`
- `encounters`
- `payments`

## Faz 1'de Kurulan Katmanlar
- `model`
- `repository`
- `dto`
- `mapper`
- `service`
- `controller`

## Faz 1'de Özellikle Kurulan Temeller
- UUID tabanlı kimlik yapısı
- UTC tabanlı tarih/saat yaklaşımı
- ortak API response standardı
- ortak error response standardı
- global exception handler
- manuel mapper yapısı
- pageable listeleme zorunluluğu

## Faz 1 Neden Önemli
Faz 1 sadece çalışan birkaç endpoint üretmek için yapılmadı. Asıl amaç, Faz 2 ve Faz 3'te yeniden yazmadan genişletilebilecek çekirdek modeli sağlam kurmaktı. Bu nedenle bazı yapılar ilk bakışta büyük görünse de sonraki fazlarda yeniden kırılmayı önlemek için erkenden oluşturuldu.
