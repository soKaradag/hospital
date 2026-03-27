# Faz 2 Kod Geliştirme Adımları

Bu doküman, Faz 2'nin kod tarafında hangi sırayla geliştirileceğini tanımlar.

## Amaç
- Her adımı küçük ve tutarlı geliştirmek
- Altyapı bağımlılıklarını doğru sırada kurmak
- Auth, audit ve domain büyütmelerini kontrollü ilerletmek

## Kesin Kurallar
- `Spring Security` kullanılmayacaktır
- Roller enum olarak tutulacaktır
- Her commit tek bir amaca hizmet edecektir
- Auth altyapısı tamamlanmadan domain genişletmesine geçilmeyecektir
- Audit altyapısı kurulmadan audit log kalıcılığı eklenmeyecektir

## Geliştirme Sırası

### Adım 1
`users` ve `user_info` yapısını ekle.

İçerik:
- `users` tablosu
- `user_info` tablosu
- entity
- repository
- role enum

### Adım 2
`refresh_tokens` yapısını ekle.

İçerik:
- `refresh_tokens` tablosu
- entity
- repository
- kullanıcı ile ilişki

### Adım 3
JWT altyapısını kur.

İçerik:
- token üretme
- token doğrulama
- access token
- refresh token üretim desteği

### Adım 4
Auth endpoint'lerini ekle.

İçerik:
- `login`
- `refresh`
- `logout`
- `me`

### Adım 5
Custom auth doğrulama katmanını kur.

İçerik:
- request bazlı current user context
- custom filter veya interceptor
- bearer token çözümleme

### Adım 6
Rol bazlı yetki kontrolünü ekle.

İçerik:
- `@RequireRole`
- authorization service
- role kontrol akışı

### Adım 7
Audit altyapısını kur.

İçerik:
- `@Audit`
- audit publisher
- observer interface
- success ve failure event akışı

### Adım 8
Audit log kalıcılığını ekle.

İçerik:
- `audit_logs` tablosu
- entity
- repository
- observer üzerinden veritabanına yazma

### Adım 9
Doctor schedule alanını ekle.

İçerik:
- `doctor_schedules` tablosu
- entity
- repository
- service
- controller

### Adım 10
Hastalık katalogu ve hasta hastalık geçmişini ekle.

İçerik:
- `diseases` tablosu
- `patient_diseases` tablosu
- ilişkiler
- service akışları

### Adım 11
Encounter teşhis ve reçete alanını ekle.

İçerik:
- `encounter_diagnoses` tablosu
- `prescriptions` tablosu
- ilişkiler
- service ve controller akışları

### Adım 12
Faz 2 testlerini ekle.

İçerik:
- auth testleri
- role kontrol testleri
- audit testleri
- yeni domain akış testleri

## Uygulama Notu
- İlk kod adımı `users` ve `user_info` ile başlamalıdır
- Her adım tamamlanmadan sonraki adıma geçilmemelidir
- Her commit sonrası ilgili minimum test çalıştırılmalıdır
