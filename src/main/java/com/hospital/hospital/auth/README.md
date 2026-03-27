# Auth Domain

Bu alan, Faz 2 ile birlikte eklenecek kimlik doğrulama ve yetkilendirme omurgasını taşır.

İlk adımda:
- `users`
- `user_info`

model ve repository katmanları eklenir.

İkinci adımda:
- `refresh_tokens`

model ve repository katmanı eklenir.

Üçüncü adımda:
- JWT access token üretimi
- JWT refresh token üretimi
- token doğrulama
- token hash desteği

altyapısı eklenir.

Dördüncü adımda:
- `login`
- `refresh`
- `logout`
- `me`

endpoint ve service akışları eklenir.

Beşinci adımda:
- `CurrentUserContext`
- auth interceptor
- web config interceptor kaydı

eklenir ve access token çözümleme request girişine taşınır.

Altıncı adımda:
- `@RequireRole`
- authorization service
- forbidden hata akışı

eklenir ve rol kontrolü request girişinde uygulanır.

Yedinci adımda:
- `@Audit`
- audit aspect
- audit publisher
- observer altyapısı

eklenir ve başarılı veya başarısız iş akışları observer pattern ile event olarak yayımlanır.

Sekizinci adımda:
- `audit_logs`
- audit log repository
- persistent audit observer

eklenir ve audit event'leri veritabanına kalıcı olarak yazılır.
