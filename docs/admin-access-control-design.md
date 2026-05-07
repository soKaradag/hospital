# Admin Access Control Design

## Hedef

Sistemde public `register` akisi olmayacak.
Yeni kullanici olusturma ve yetki atama sadece admin tarafindan yapilacak.
Login ekrani herkese acik kalacak ancak sadece admin tarafindan olusturulmus ve aktif kullanicilar giris yapabilecek.

Temel akış:

1. Admin role olusturur.
2. Admin role permission atar.
3. Admin kullanici olusturur.
4. Admin kullaniciya bir veya daha fazla role atar.
5. Doktor veya ilgili role sahip kullanici login olur.
6. UI ve API erisimi efektif permission kumesine gore belirlenir.

## Mevcut Durum

- Backend'de `roles`, `permissions`, `role_permissions`, `user_roles` tablolari zaten var.
- `PermissionResolutionService` kullanicinin efektif permission kumesini hesapliyor.
- `/api/auth/login`, `/api/auth/refresh`, `/api/auth/logout`, `/api/auth/me` mevcut.
- Frontend'de `register` sayfasi yok.
- Varsayilan `admin / admin123` seed ile olusuyor.

Eksik kisim, admin odakli role, permission ve user yonetim yuzeyi ile buna ait CRUD endpointleridir.

## Hedef Mimari

### 1. Auth yuzeyi

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`

Public tarafta yalnizca login kalir.
`register`, `self-signup`, `invite accept` gibi public account olusturma yuzeyleri bulunmaz.

### 2. Admin Access Control modulu

Yeni yonetim modulu:

- `Access Control > Roles`
- `Access Control > Users`

Istege bagli olarak `Permissions` sadece read-only katalog ekrani olabilir.

## Permission modeli

Permission'lar admin tarafindan serbest metin olarak uretilmez.
Permission kodlari backend kodunda hazir gelir ve sistemin tek kaynagi kod tarafidir.

Kaynak:

- `com.hospital.hospital.auth.model.PermissionCodes`

Yani model su sekilde olacaktir:

- Permission listesi kodda sabit/enum-benzeri olarak tanimlanir.
- Flyway seed bu kodlardan beslenir.
- Admin UI yeni permission olusturmaz.
- Admin sadece hazir permission listesinden role atama yapar.

Mevcut domain permission'lar korunur.
Bunlara ek olarak yonetim permission kodlari da kodda tanimlanmalidir:

- `access-control.roles.read`
- `access-control.roles.write`
- `access-control.permissions.read`
- `access-control.users.read`
- `access-control.users.write`

Beklenen politika:

- `ADMIN`: tum access-control permission'larina sahip olur.
- `DOCTOR`: sadece klinik is akislari icin gerekli permission'lari alir.
- Diger roller operasyonel ihtiyaca gore minimal permission seti alir.

## Backend tasarimi

### 1. Role management

Yeni endpointler:

- `GET /api/admin/roles`
- `GET /api/admin/roles/{id}`
- `POST /api/admin/roles`
- `PUT /api/admin/roles/{id}`
- `PUT /api/admin/roles/{id}/permissions`

Kurallar:

- `code` benzersiz olmali.
- `isSystemRole=true` olan roller silinmemeli.
- Sistem rollerinin kritik alanlari sinirli sekilde guncellenmeli.
- Permission atamasi replace mantigiyla yapilmali.

### 2. Permission catalog

Yeni endpoint:

- `GET /api/admin/permissions`

Amac:

- Kodda tanimli hazir permission listesini admin UI'a vermek.
- UI tarafinda gruplama yapmak.

Not:

- Bu endpoint DB'den veya dogrudan `PermissionCodes` sabitlerinden beslenebilir.
- Ancak izin tanimlama otoritesi kod tarafinda kalmalidir.
- Permission create, update, delete endpointi olmayacaktir.

Gruplama ornegi:

- `auth.*`
- `access-control.*`
- `departments.*`
- `doctors.*`
- `patients.*`
- `appointments.*`
- `encounters.*`
- `payments.*`
- `doctor-schedules.*`
- `diseases.*`
- `patient-diseases.*`
- `encounter-diagnoses.*`
- `prescriptions.*`
- `reports.*`
- `inventory.*`
- `surgeries.*`

### 3. User management

Yeni endpointler:

- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `PUT /api/admin/users/{id}/roles`
- `PUT /api/admin/users/{id}/password`
- `PATCH /api/admin/users/{id}/status`

Onerilen request modeli:

- `POST /api/admin/users`
  - `username`
  - `password`
  - `firstName`
  - `lastName`
  - `email`
  - `phoneCountryCode`
  - `phoneNumber`
  - `roleIds`
  - `primaryRoleId`

Ek alan onerisi:

- `status`: `ACTIVE`, `PASSIVE`, `LOCKED`

Not:

Bugun `users.role` enum kolonu gecis amacli duruyor.
Hedef durumda primary role ile senkron tutulabilir veya cleanup asamasinda tamamen kaldirilabilir.

### 4. Login kurallari

`AuthService.login` asagidaki ek kurallari uygulamali:

- Kullanici yoksa reddet.
- Sifre yanlissa reddet.
- Kullanici `ACTIVE` degilse reddet.
- Kullaniciya en az bir role atanmamissa reddet.
- Efektif permission kumesi bos ise opsiyonel olarak reddet veya kisitli giris ver.

## Frontend tasarimi

### 1. Login

`/login` sade kalir:

- Username
- Password
- Sign in

Login ekraninda `Create account` veya `Register` linki bulunmaz.
Mevcut login sayfasi bu hedefle uyumludur.

### 2. Navigation

Yeni menu grubu:

- `Administration`
  - `Roles`
  - `Users`

Gorunurluk:

- `Roles` icin `access-control.roles.read`
- `Users` icin `access-control.users.read`

### 3. Roles page

Liste kolonlari:

- Role name
- Code
- Type (`System` veya `Custom`)
- Permission count
- Updated at

Detay veya form:

- Basic info
- Koddan gelen hazir permission checklist
- Save

### 4. Users page

Liste kolonlari:

- Username
- Full name
- Email
- Status
- Primary role
- Role count

Detay veya form:

- Profile bilgileri
- Gecici veya kalici sifre
- Role multi-select
- Primary role secimi
- Active/passive durumu

### 5. Doctor kullanici deneyimi

Admin su sekilde calisir:

1. `Doctor` rolunu olusturur veya mevcut sistem rolunu kullanir.
2. Bu role `doctors.read`, `doctor-schedules.read`, `encounters.read`, `prescriptions.write` gibi ihtiyaca uygun permission'lari verir.
3. Doktor kullanicisini olusturur.
4. Doktora `DOCTOR` rolunu atar.

Sonrasinda doktor login oldugunda:

- Sidebar sadece yetkili oldugu menuleri gosterir.
- Endpoint cagrilarinda `403` yerine proaktif gizleme uygulanir.
- Kritik alanlarda backend yine `@RequirePermission` ile son karari verir.

## Domain entegrasyonu

Doktor entity ile auth user kaydi birbirine baglanmali.
Onerilen alan:

- `doctors.user_id`

Boylece:

- Bir doktor kaydi ile login olabilen kullanici eslenebilir.
- Takvim, encounter ve prescription akislarinda "giris yapan doktor" ile domain doktoru baglanabilir.

Bu bag bir sonraki asamada eklenebilir; access-control modulunun ilk versiyonu bu iliski olmadan da calisabilir.

## Mevcut yapiya entegrasyon sureci

Bu tasarim sifirdan yeni bir auth sistemi kurmaz.
Mevcut yapinin ustune oturur ve asagidaki mevcut parcalari genisletir:

- `AuthServiceImpl`: login, me, refresh akislarinin merkezi
- `PermissionResolutionServiceImpl`: rol ve efektif permission cozumleme noktasi
- `PermissionCodes`: permission tanimlarinin kod kaynagi
- `AdminSeedConfig`: ilk admin olusturma ve admin rol baglama noktasi
- `navigation.ts`: frontend menu yetki gorunurlugu noktasi
- `auth-provider.tsx`: login sonrasi user ve permission state yukleme noktasi

Entegrasyonun ana prensibi:

- Login akisini bozma.
- Mevcut RBAC cozumleyiciyi yeniden yazma.
- Yeni yonetim katmanini mevcut permission-first omurgaya ekle.
- `users.role` alanini bir gecis katmani olarak bir sure daha koru.

### Entegrasyon adimlari

### Adim 1 - Permission kodlarini genislet

Ilk is olarak `PermissionCodes` sinifina yeni yonetim kodlari eklenir:

- `access-control.roles.read`
- `access-control.roles.write`
- `access-control.permissions.read`
- `access-control.users.read`
- `access-control.users.write`

Bu adimda:

- Backend compile-time sabitler hazir olur.
- Frontend menu ve page guard'larinda kullanilacak ortak isimler netlesir.
- Sonraki migration ve endpointler bu sabitlere dayanir.

### Adim 2 - Permission seed ile DB'yi kodla hizala

Flyway migration eklenir ve yeni permission kayitlari `permissions` tablosuna yazilir.
Ardindan `ADMIN` rolune bu permission'lar atanir.

Bu adimda hedef:

- Kodda var olan permission'in DB'de de bulunmasi
- `PermissionResolutionServiceImpl` tarafinda ekstra degisiklik gerekmemesi

Burada kritik kural:

- Kodda olmayan permission DB'ye eklenmemeli.
- DB'de olup kodda olmayan permission varsa cleanup listesine alinmali.

### Adim 3 - User status alanini ekle

`users` tablosuna yeni bir durum kolonu eklenir:

- `status`

Onerilen enum:

- `ACTIVE`
- `PASSIVE`
- `LOCKED`

Bu adimda dokunulacak alanlar:

- Flyway migration
- `User` entity
- user create/update DTO'lari
- login kurali

Gecis kurali:

- Eski kayitlar `ACTIVE` olarak initialize edilir.

### Adim 4 - Login akisina yeni kurallari bagla

`AuthServiceImpl.login` icine su kontroller eklenir:

- Kullanici aktif mi
- Kullaniciya en az bir rol atanmis mi
- Gerekirse hesap locked mi

Burada mevcut sifre ve token mantigi korunur.
Sadece giris oncesi ek guard eklenir.

Bu adim neden once yapilmiyor:

- Cunku status ve rol verisi DB'de tam hazir olmadan login kisitlari erken devreye girerse mevcut kullanicilar yanlislikla bloke olabilir.

### Adim 5 - Admin backend modulunu ekle

Yeni bir admin domain paketi acilir.
Onerilen paket:

- `com.hospital.hospital.accesscontrol`

Alt katmanlar:

- `controller`
- `dto`
- `service`
- `mapper`

Mevcut auth repository'leri tekrar kullanilir:

- `UserRepository`
- `UserInfoRepository`
- `RoleEntityRepository`
- `PermissionRepository`
- `UserRoleRepository`

Ihtiyaca gore yeni repository'ler eklenir:

- `RolePermissionRepository`

Bu moduldaki servisler:

- `RoleAdminService`
- `UserAdminService`
- `PermissionCatalogService`

Bu sayede auth login mantigi ile admin CRUD mantigi birbirine karismaz.

### Adim 6 - Role management endpointlerini ekle

Ilk aktif yonetim yuzeyi role management olmali.
Cunku user olusturma bundan sonra role baglanacak.

Sira:

1. `GET /api/admin/permissions`
2. `GET /api/admin/roles`
3. `POST /api/admin/roles`
4. `PUT /api/admin/roles/{id}`
5. `PUT /api/admin/roles/{id}/permissions`

Koruma:

- `@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_READ)` benzeri sabitler kullanilir.
- Write endpointleri yalnizca write permission ile acilir.

### Adim 7 - User management endpointlerini ekle

Role akisi oturduktan sonra kullanici yonetimi gelir.

Sira:

1. `GET /api/admin/users`
2. `GET /api/admin/users/{id}`
3. `POST /api/admin/users`
4. `PUT /api/admin/users/{id}`
5. `PUT /api/admin/users/{id}/roles`
6. `PUT /api/admin/users/{id}/password`
7. `PATCH /api/admin/users/{id}/status`

Burada dikkat:

- Kullanici olusurken en az bir role atanmasi tercih edilir.
- `primaryRoleId` verilen roller arasinda olmak zorundadir.
- `users.role` alanina primary role mirror yazilabilir.

### Adim 8 - `users.role` gecis stratejisi

Mevcut sistemde `User.role` enum alani hala var.
Bu alan bir sure daha tutulmali; cunku `PermissionResolutionServiceImpl` user role kaydi yoksa fallback olarak buradan besleniyor.

Gecis stratejisi:

1. Yeni kullanici olusurken hem `user_roles` hem `users.role` doldurulur.
2. Eski kullanicilar icin eksik `user_roles` kayitlari backfill edilir.
3. Tumu tasindiktan sonra fallback ihtiyaci tekrar degerlendirilir.

Bu asamada fallback'i hemen kaldirmak risklidir.

### Adim 9 - Frontend navigation entegrasyonu

`frontend/src/ui/components/navigation.ts` genisletilir.
Yeni grup:

- `Administration`

Yeni item'lar:

- `/admin/roles`
- `/admin/users`

Gorunurluk permission'lari:

- `access-control.roles.read`
- `access-control.users.read`

Mevcut `getNavigationSections` yapisi yeniden kullanilabilir.
Sadece grup tipi genisletilir.

### Adim 10 - Frontend sayfa entegrasyonu

`frontend/src/app.tsx` icine yeni route'lar eklenir:

- `/admin/roles`
- `/admin/users`

Sayfalar:

- `roles-list-page.tsx`
- `role-form-page.tsx`
- `users-list-page.tsx`
- `user-form-page.tsx`

Bu ekranlar mevcut liste-form desenini takip etmelidir.
Boylece UI dili bozulmaz.

### Adim 11 - Auth state ile UI davranisini birlestir

`auth-provider.tsx` ve `/auth/me` cevabi zaten `roles` ve `permissions` donuyor.
Bu nedenle yeni bir auth modeli yazmaya gerek yok.

Yapilacaklar:

- Yeni admin menu item'larini mevcut permission listesiyle filtrele
- Role ve user ekranlarinda buton gorunurlugunu write permission ile kontrol et
- Sayfa acilisinda backend `403` donerse tutarli hata ekrani goster

### Adim 12 - Doctor domain entegrasyonu

Ilk surumde doktor kullanicisi sadece auth user olarak olusabilir.
Ikinci asamada `doctors.user_id` baglantisi eklenir.

Sira:

1. nullable `doctors.user_id` migration
2. `Doctor` entity guncellemesi
3. doktor olusturma veya duzenleme ekraninda auth user secimi
4. login olan doktor ile domain doktor esleme servisi

Bu adim access-control modulunun ilk acilisi icin zorunlu degildir.

## Rollout plani

### Sprint 1 - Altyapi uyumu

- `PermissionCodes` genislet
- migration ile permission seed ekle
- `users.status` ekle
- login guard'larini guncelle

Cikis kriteri:

- Eski login bozulmaz
- admin yeni permission'lari alir

### Sprint 2 - Backend admin CRUD

- permission catalog endpoint
- role CRUD ve role-permission atama
- user CRUD ve user-role atama

Cikis kriteri:

- Postman veya integration test ile admin yeni user/role yonetebilir

### Sprint 3 - Frontend admin panelleri

- admin navigation
- roles list/form
- users list/form

Cikis kriteri:

- UI uzerinden role olustur, permission ata, user olustur, role ata akisi tamamlanir

### Sprint 4 - Domain baglantilari

- `doctors.user_id`
- doktor oturumu ile doktor domain esleme

Cikis kriteri:

- login olan doktor ile domain doktoru ayni kimlik zincirine baglanir

## Veri gecis plani

Eski veri varsa su sirayla tasinmali:

1. `roles` ve `permissions` tablolarini dogrula
2. `ADMIN` kullanicisinin `user_roles` kaydini dogrula
3. `users.role` degeri olan ama `user_roles` kaydi olmayan kullanicilari backfill et
4. Tum kullanicilara varsayilan `ACTIVE` status ata
5. Yeni access-control permission'larini `ADMIN` rolune bagla

Backfill mantigi:

- `users.role = DOCTOR` ise `DOCTOR` role relation olustur
- `users.role = ADMIN` ise `ADMIN` role relation olustur
- primary role flag true olarak set edilir

## Test ve dogrulama plani

### Backend

- `AuthServiceImplTest`
  - inactive user login olamaz
  - role'suz user login olamaz
- role admin service testleri
  - kodda olmayan permission atanamaz
  - system role silinemez
- user admin service testleri
  - primary role zorunlulugu
  - status gecis kurallari

### Frontend

- admin permission varsa menu gorunur
- admin permission yoksa menu gorunmez
- role ekraninda hazir permission listesi gelir
- user ekraninda role atama calisir

### Manual smoke

1. admin login olur
2. custom role olusturur
3. role permission atar
4. user olusturur
5. user'a role atar
6. yeni user login olur
7. sadece yetkili ekranlari gorur

## Riskler ve kararlar

- `users.role` fallback'i erken kaldirmak login regressioni yaratabilir.
- Permission'lari hem kod hem DB'de yonetirken drift olusabilir; kod ana kaynak olmalidir.
- Son admin kullanicisinin kilitlenmesi operasyonel risk yaratir; backend guard gerekir.
- UI gizleme yeterli degildir; backend `@RequirePermission` korunmaya devam etmelidir.

## Guvenlik ve operasyon notlari

- Sifreler sadece hash olarak saklanmali.
- User create ve password reset islemleri auditlenmeli.
- Role permission degisiklikleri auditlenmeli.
- Kodda olmayan bir permission DB veya API uzerinden atanamamalidir.
- Admin kendi rolunu yanlislikla sifir permission'a dusurememeli.
- Son aktif admin kullanicisinin pasife alinmasi engellenmeli.
- `systemRole` olan kayitlar silinmemeli.

## Uygulama plani

### Faz 1

- Public register yuzeyi olmadigini sabitle.
- Yeni access-control permission kodlarini ekle.
- Permission seed migration'i yaz.

### Faz 2

- Role, permission ve user admin DTO/controller/service katmanini ekle.
- User status alanini ekle.
- Login akisina aktiflik kontrolu koy.

### Faz 3

- Frontend `Administration > Roles` sayfasini ekle.
- Frontend `Administration > Users` sayfasini ekle.
- Navigation permission kontrolunu yeni sayfalara uygula.

### Faz 4

- `doctors.user_id` iliskisini ekle.
- Doktor oturumu ile domain doktorunu bagla.
- Gerekirse "Benim randevularim" gibi personel odakli ekranlari ac.

## Kabul kriterleri

- Sistemde register sayfasi veya endpointi bulunmaz.
- Admin yeni custom role olusturabilir.
- Admin bir role birden fazla permission atayabilir.
- Admin yeni kullanici olusturabilir.
- Admin kullaniciya birden fazla role atayabilir.
- Doktor veya baska role sahip kullanici login olabilir.
- Login sonrasi sadece yetkili menu ve ekranlar gorunur.
- Backend permission guard'lari erisim kontrolunu korur.
