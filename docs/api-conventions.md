# API Kuralları

Bu doküman, Faz 1 itibarıyla kullanılacak ortak API response ve error yapısını tanımlar.

## Başarılı Cevap Formatı

Tüm başarılı cevaplar ortak bir zarf yapısı ile döner.

Örnek alanlar:
- `success`
- `message`
- `data`
- `timestamp`

Örnek yapı:

```json
{
  "success": true,
  "message": "Patient created successfully",
  "data": {
    "id": "7d74c2dd-7f14-4f6a-91a4-b4c1833c12ae"
  },
  "timestamp": "2026-03-26T12:00:00Z"
}
```

## Hata Cevap Formatı

Tüm hata cevapları ortak bir yapı ile döner.

Örnek alanlar:
- `success`
- `code`
- `message`
- `errors`
- `timestamp`

Örnek yapı:

```json
{
  "success": false,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "errors": [
    {
      "field": "firstName",
      "rejectedValue": "",
      "message": "firstName must not be blank"
    }
  ],
  "timestamp": "2026-03-26T12:00:00Z"
}
```

## Faz 1 İçin Ortak Hata Kodları
- `VALIDATION_ERROR`
- `RESOURCE_NOT_FOUND`
- `BUSINESS_RULE_VIOLATION`
- `DUPLICATE_RESOURCE`
- `UNAUTHORIZED`
- `FORBIDDEN`
- `INTERNAL_SERVER_ERROR`

## Kullanım İlkeleri
- Başarılı cevaplarda `success` her zaman `true` olur.
- Hata cevaplarında `success` her zaman `false` olur.
- `message` kısa ve anlaşılır olmalıdır.
- `timestamp` UTC olmalıdır.
- `errors` alanı sadece detay listesi gerektiğinde doldurulmalıdır.
- Domain'e özel hata metinleri olabilir, fakat hata kodları ortak kalmalıdır.
