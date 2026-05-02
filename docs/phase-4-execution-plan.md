# Faz 4 - Inventory Service Yurutme Plani

Bu dokuman, Faz 4 tasarimini kod tarafina tasimak icin ayrintili gelistirme sirasini ve oncelikli use case setini tanimlar.

Odak, mevcut `hospital-core` servisini bozmadan yeni bir `inventory-service` eklemek, klinik stok ile facility stok ayrimini netlestirmek ve ameliyat/prosedur entegrasyonuna hazir bir omurga kurmaktir.

## Guncel Durum

- `hospital-core` su anda `56` tabloya ulasmistir.
- `inventory-service` su anda `22` tabloya ulasmistir.
- Permission-first RBAC aktif durumdadir.
- Faz 4 tasarimi kod tarafina tasinmistir.
- Inventory, klinik tuketim ve cerrahi rezervasyon akislarinin local smoke otomasyonu hazirdir.

## Faz 4 Hedefi

- Yeni servis: `inventory-service`
- Yeni inventory tablo sayisi: `22`
- Onerilen cerrahi core genislemesi: `8` tablo
- Hedef ekosistem toplam tablo sayisi: `78`

## Uygulama Kurallari

- `hospital-core` ve `inventory-service` ayri veritabani/schema kullanir.
- Servisler tablo paylasmaz.
- Ilk surum entegrasyonu sync HTTP ile kurulur.
- Event tabanli akis ancak sync kontratlar sabitlendikten sonra dusunulur.
- Her adim sonunda calisir build alinmalidir.
- Her adim sonunda tek amacli commit atilmalidir.
- Bir adim hem inventory hem core hem de cerrahiyi ayni anda buyutmeye calismamalidir.

## Basarinin Olculeri

- `inventory-service` tek basina acilip migrationlarini uygulayabilir.
- Facility stok yonetimi, `hospital-core`e bagli olmadan calisabilir.
- Klinik stok entegrasyonlari sadece gerekli use case'lerde tetiklenir.
- `hospital-core`, stok gerceginin sahibi olmadan inventory sonucunu kullanabilir.
- Ameliyat use case'leri icin rezervasyon, tuketim ve iptal akislarinin kontrati netlesmis olur.

## Local Runtime ve Smoke Kontrati

Varsayilan local sozlesme:
- Core DB: `hospital`
- Inventory DB: `hospital_inventory`
- `hospital-core`: `http://127.0.0.1:8080`
- `inventory-service`: `http://127.0.0.1:8081`
- Varsayilan admin kullanicisi: `admin / admin123`

Temel komutlar:
1. `bash scripts/phase4-db-reset.sh`
2. `bash scripts/phase4-up.sh`
3. `bash scripts/phase4-smoke.sh`

`phase4-smoke.sh` su adimlari tek shell icinde sirayla yapar:
1. core ve inventory DB'lerini resetler
2. versioned SQL migrationlarini uygular
3. `inventory-service`i baslatir
4. `hospital-core`u baslatir
5. `inventory-standalone-smoke.py` akisini kosar
6. `phase4-clinical-smoke.py` akisini kosar

Beklenen sonuc:
- script sonunda `Phase 4 dual-service smoke passed.` gorulur
- inventory standalone smoke category -> warehouse -> supplier -> item -> purchase order -> goods receipt -> availability akisini gecer
- clinical smoke encounter tuketimi, prescription tuketimi ve surgery reserve/cancel/complete akisini gecer

2026-05-02 dogrulama durumu:
- `./mvnw -q -DskipTests compile` gecti
- `./mvnw -q -f inventory-service/pom.xml -DskipTests compile` gecti
- odakli core ve inventory testleri gecti
- `bash scripts/phase4-smoke.sh` gecti

## Oncelikli Use Case Seti

### UC-1 Depo ve Stok Kalemi Tanimlama

Actor:
- inventory admin

Amaç:
- yeni depo, zone, kategori ve stok kalemi tanimlamak

Basarili akis:
1. admin depo olusturur
2. ilgili zone'lari tanimlar
3. stok kalemi ve birim donusumlerini ekler
4. item barkod ve alias bilgileri baglanir

Basarisiz akis:
- ayni depo kodu veya ayni item kodu tekrar olusturulamaz

Tablolar:
- `warehouses`
- `warehouse_zones`
- `inventory_categories`
- `inventory_items`
- `inventory_item_units`
- `inventory_item_aliases`
- `inventory_item_barcodes`

### UC-2 Tedarikciden Satin Alma ve Mal Kabul

Actor:
- procurement user
- warehouse clerk

Amaç:
- satin alma siparisi acmak ve gelen urunleri batch bazli stoga almak

Basarili akis:
1. tedarikci secilir
2. purchase order acilir
3. urunler satin alma satirlarina eklenir
4. teslimatta goods receipt olusturulur
5. batch, SKT ve miktar girilir
6. stok hareketleri otomatik yazilir

Basarisiz akis:
- siparis satiri olmayan urun mal kabule alinmaz
- gecmis tarihli SKT icin blok veya warning kuralı calisir

Tablolar:
- `suppliers`
- `supplier_catalog_items`
- `purchase_orders`
- `purchase_order_items`
- `goods_receipts`
- `goods_receipt_items`
- `stock_batches`
- `stock_movements`

### UC-3 Facility Stok Transferi

Actor:
- warehouse operator

Amaç:
- merkez depodan birim deposuna stok transfer etmek

Basarili akis:
1. transfer talebi acilir
2. kaynak depo uygunlugu kontrol edilir
3. transfer onaylanir
4. cikis ve giris hareketleri yazilir

Basarisiz akis:
- yetersiz stokta transfer tamamlanmaz

Tablolar:
- `stock_transfer_requests`
- `stock_transfers`
- `stock_movements`
- `stock_batches`

### UC-4 Sayim ve Duzeltme

Actor:
- inventory auditor

Amaç:
- fiziksel sayim yapip sistem farklarini kapatmak

Basarili akis:
1. sayim oturumu baslatilir
2. satirlar girilir
3. farklar hesaplanir
4. onayli farklar adjustment olarak yazilir

Basarisiz akis:
- kapatilmis sayim tekrar duzenlenemez

Tablolar:
- `stock_counts`
- `stock_count_lines`
- `stock_adjustments`
- `stock_movements`

### UC-5 Recete Dispense ile Klinik Stok Dusumu

Actor:
- pharmacy user
- `hospital-core`

Amaç:
- dispense edilen ilacin fiziksel stoktan dogru batch ile dusulmesi

Basarili akis:
1. `hospital-core` dispense kaydi olusturur
2. inventory'ye consume istegi gonderir
3. inventory uygun batch secimini yapar
4. stok hareketi yazilir
5. sonuc core'a dondurulur

Basarisiz akis:
- stok yetersizse inventory red cevabi verir
- core dispense kaydini `pending_inventory` benzeri duruma cekebilir

Tablolar:
- `stock_batches`
- `stock_movements`
- `stock_reservations`

### UC-6 Encounter Procedure Tuketimi

Actor:
- clinic nurse
- `hospital-core`

Amaç:
- prosedur sirasinda kullanilan sarflarin stoktan dusulmesi

Basarili akis:
1. procedure kaydi acilir
2. gerekli sarf kalemleri inventory'ye iletilir
3. inventory batch bazli tuketim kaydeder
4. sonuc procedure kaydina baglamsal olarak doner

Basarisiz akis:
- kritik sarf eksiginde klinik akisa warning doner

Tablolar:
- `stock_movements`
- `stock_batches`

### UC-7 Ameliyat Icin Pre-Op Rezervasyon

Actor:
- surgery coordinator
- `hospital-core`

Amaç:
- planlanan ameliyat icin gerekli sarflari onceden ayirtmak

Basarili akis:
1. ameliyat planlanir
2. supply template secilir
3. core inventory'ye reservation istegi gonderir
4. inventory gerekli batchleri ayirir
5. rezervasyon durumu ameliyat kaydina yansitilir

Basarisiz akis:
- eksik stok varsa kismi rezervasyon veya red stratejisi uygulanir

Tablolar:
- `stock_reservations`
- `stock_batches`
- `stock_movements`

### UC-8 Ameliyat Tamamlama ve Gercek Tuketim

Actor:
- surgery nurse
- `hospital-core`

Amaç:
- ameliyatta fiilen kullanilan urunlerin dusulmesi ve kullanilmayan rezervasyonun serbest birakilmasi

Basarili akis:
1. ameliyat tamamlanir
2. kullanilan item'lar bildirilir
3. inventory rezervasyonu tuketime cevirir
4. artan rezervasyonlar serbest kalir

Basarisiz akis:
- rezervasyonla fiili kullanim uyusmuyorsa reconciliation kaydi olusur

Tablolar:
- `stock_reservations`
- `stock_movements`
- `stock_adjustments`

### UC-9 Yeniden Siparis Tetikleme

Actor:
- inventory planner

Amaç:
- stok belirli esigin altina dusunce tedarik planini otomatiklestirmek

Basarili akis:
1. item icin reorder rule tanimlanir
2. eldeki stok esik altina iner
3. sistem replenishment onerisi uretir
4. procurement kullanicisi purchase order acabilir

Tablolar:
- `reorder_rules`
- `stock_batches`
- `stock_movements`
- `purchase_orders`

## Bolum 1 - Inventory Service Temeli

Bu bolumun amaci yeni servisi yalniz calisir hale getirmek ve temel master data katmanini kurmaktir.

### 1.1 Servis Iskeleti

#### Adim 1 - Inventory service proje iskeleti

Kapsam:
- yeni Spring Boot servis yapisi
- package ve domain klasorleri
- ortak response/error yapisi
- temel health endpoint

Teslim:
- calisan servis iskeleti
- temel config dosyalari
- Docker ve local run notlari

Commit basligi:
`Create inventory service foundation`

Commit mesaji:
`Create the initial inventory-service skeleton with baseline application structure, shared API response models and local run configuration.`

#### Adim 2 - Inventory auth ve permission omurgasi

Kapsam:
- JWT dogrulama yaklasimi
- inventory permission annotation yapisi
- temel `inventory.*` permission kodlari

Teslim:
- auth filter/interceptor
- permission check altyapisi
- korumali ilk endpointler

Commit basligi:
`Add inventory permission guard foundation`

Commit mesaji:
`Introduce JWT-based request authentication and permission-first authorization guards for inventory-service endpoints.`

### 1.2 Migration ve Master Data

#### Adim 3 - Flyway baseline ve master data ilk grup

Kapsam:
- Flyway kurulumu
- `warehouses`
- `warehouse_zones`
- `inventory_categories`
- `suppliers`

Teslim:
- baseline migration
- ilk repository ve service katmanlari
- temel CRUD endpointleri

Commit basligi:
`Add inventory master data baseline`

Commit mesaji:
`Introduce Flyway baseline plus warehouse, zone, category and supplier master data for inventory-service.`

#### Adim 4 - Item katalogu ve birim donusumleri

Kapsam:
- `inventory_items`
- `inventory_item_units`
- `inventory_item_aliases`
- `inventory_item_barcodes`

Teslim:
- item CRUD
- barcode ve alias sorgulari
- birim cevrim kurallari

Commit basligi:
`Build inventory item catalog model`

Commit mesaji:
`Add inventory item catalog tables with unit conversions, aliases and barcodes to support operational stock identification.`

## Bolum 2 - Stok Operasyonlari

Bu bolumun amaci inventory-service'i yalniz basina gercek stok yoneten bir servise donusturmektir.

### 2.1 Batch ve Hareket Omurgasi

#### Adim 5 - Batch ve movement ledger

Kapsam:
- `stock_batches`
- `stock_movements`
- giris/cikis hareket tipleri
- batch bazli stok hesaplama

Teslim:
- receipt ve manual movement altyapisi
- item bazli availability sorgusu

Commit basligi:
`Add batch-based stock ledger`

Commit mesaji:
`Introduce stock_batches and stock_movements as the batch-aware ledger foundation of inventory-service.`

#### Adim 6 - Rezervasyon yapisi

Kapsam:
- `stock_reservations`
- reservation create/release akisi
- quantity hold kurallari

Teslim:
- reservation API'leri
- core entegrasyonuna hazir kontrat

Commit basligi:
`Add stock reservation workflow`

Commit mesaji:
`Add stock_reservations and reservation lifecycle logic for future clinical and surgical stock allocation flows.`

### 2.2 Duzeltme, Transfer ve Sayim

#### Adim 7 - Manuel adjustment yapisi

Kapsam:
- `stock_adjustments`
- neden kodlari
- movement entegrasyonu

Teslim:
- adjustment endpointleri
- audit-friendly hareket baglantisi

Commit basligi:
`Introduce stock adjustment controls`

Commit mesaji:
`Add stock_adjustments to capture manual corrections and counting differences through explicit inventory controls.`

#### Adim 8 - Transfer talebi ve transfer akisi

Kapsam:
- `stock_transfer_requests`
- `stock_transfers`
- kaynak/hedef depo kurallari

Teslim:
- transfer isteme
- onaylama ve uygulama akisi

Commit basligi:
`Add stock transfer workflow`

Commit mesaji:
`Introduce stock transfer requests and executions so inventory can move safely between warehouses and zones.`

#### Adim 9 - Sayim oturumu ve satirlari

Kapsam:
- `stock_counts`
- `stock_count_lines`
- count close kurallari

Teslim:
- count baslatma
- sayim satiri girisi
- farklarin adjustment'a donusmesi

Commit basligi:
`Add inventory counting workflow`

Commit mesaji:
`Introduce stock_counts and stock_count_lines to support structured physical counting and discrepancy resolution.`

## Bolum 3 - Tedarik ve Planlama

Bu bolumun amaci tedarik akislarini stok ledger ile birlestirmek ve yeniden siparis kararlarini olgunlastirmaktir.

### 3.1 Satin Alma ve Mal Kabul

#### Adim 10 - Supplier catalog ve purchase order

Kapsam:
- `supplier_catalog_items`
- `purchase_orders`
- `purchase_order_items`

Teslim:
- supplier-item esleme
- purchase order CRUD
- durum gecisleri

Commit basligi:
`Add procurement ordering workflow`

Commit mesaji:
`Introduce supplier catalog mappings and purchase order tables for controlled procurement planning.`

#### Adim 11 - Goods receipt ve stok girisi

Kapsam:
- `goods_receipts`
- `goods_receipt_items`
- PO'dan mal kabule gecis
- otomatik batch ve movement olusturma

Teslim:
- goods receipt API
- kabul aninda stok artirma mantigi

Commit basligi:
`Add goods receipt processing`

Commit mesaji:
`Introduce goods receipt tables and connect them to batch creation and inbound stock movement processing.`

### 3.2 Yeniden Siparis ve Raporlama

#### Adim 12 - Reorder rule mekanizmasi

Kapsam:
- `reorder_rules`
- threshold kontrolleri
- low stock sorgulari

Teslim:
- reorder rule CRUD
- planner odakli listeleme

Commit basligi:
`Add reorder planning rules`

Commit mesaji:
`Introduce reorder_rules to enable threshold-based replenishment planning for critical inventory items.`

## Bolum 4 - Hospital Core Entegrasyonu

Bu bolumun amaci inventory-service'i once klinik olmayan use case'lerde olgunlastirip sonra kontrollu bicimde `hospital-core` ile baglamaktir.

### 4.1 Klinik Disi Olgunlastirma

#### Adim 13 - Facility stok use case'leri ile stabilizasyon

Kapsam:
- temizlik ve genel depo item'lari ile smoke senaryolari
- inventory'nin core bagimsiz dogrulanmasi

Teslim:
- demo seed verileri
- facility stok smoke akislari

Commit basligi:
`Stabilize inventory with facility stock scenarios`

Commit mesaji:
`Add facility-oriented seed data and smoke scenarios to validate inventory-service independently from clinical integrations.`

### 4.2 Klinik Entegrasyon Kontratlari

#### Adim 14 - Prescription dispense entegrasyon kontrati

Kapsam:
- `hospital-core` -> inventory consume API istemcisi
- dispense sonucu inventory status modeli

Teslim:
- sync client contract
- hata ve retry stratejisi

Commit basligi:
`Integrate prescription dispense stock consumption`

Commit mesaji:
`Connect prescription dispense flows to inventory-service consumption APIs with explicit success, failure and pending states.`

#### Adim 15 - Encounter procedure tuketim kontrati

Kapsam:
- procedure tabanli sarf dusumu
- minimum warning davranisi

Teslim:
- procedure consumption client
- baglamsal response guncellemeleri

Commit basligi:
`Integrate procedure-driven supply consumption`

Commit mesaji:
`Connect encounter procedure flows to inventory-service so clinical supply usage can be recorded against physical stock.`

### 4.3 Cerrahi Hazirlik

#### Adim 16 - Cerrahi cekirdek tablolarinin eklenmesi

Kapsam:
- `operating_rooms`
- `surgery_requests`
- `surgeries`
- `surgery_team_assignments`
- `surgery_status_history`

Teslim:
- surgery core modelleri
- planlama ve ekip atama akislarinin ilk surumu

Commit basligi:
`Add surgical scheduling core`

Commit mesaji:
`Introduce operating rooms and core surgery scheduling tables to prepare the hospital-core for surgical workflows.`

#### Adim 17 - Doktor prosedur yetkinligi ve supply template yapisi

Kapsam:
- `doctor_procedure_privileges`
- `surgery_supply_templates`
- `surgery_supply_template_items`

Teslim:
- operator/yardimci uygunluk kurallari
- ameliyat tipine gore stok template tanimi

Commit basligi:
`Model surgical privileges and supply templates`

Commit mesaji:
`Add doctor procedure privileges and surgery supply templates to model operator eligibility and expected surgical supplies.`

#### Adim 18 - Ameliyat rezervasyon ve completion entegrasyonu

Kapsam:
- pre-op reservation
- cancellation release
- completion consumption

Teslim:
- surgery -> inventory reservation client
- surgery close -> inventory consume/release akisi

Commit basligi:
`Integrate surgery inventory reservation flows`

Commit mesaji:
`Connect surgical planning and completion flows to inventory-service reservation, release and consumption APIs.`

## Test ve Dogrulama Plani

- Her adim sonunda minimum ilgili servis compile komutu
- Migration adimlarinda temiz DB bootstrap
- Inventory icin repository ve service testleri
- Stok hesaplamasi icin batch/movement unit testleri
- Transfer ve count akislari icin integration testleri
- `hospital-core` istemci kontratlari icin stub veya mock tabanli testler
- Ameliyat use case'lerinde reservation, release ve completion senaryolari

## Faz 4 Sonunda Beklenen Cikti

- `inventory-service` ayri deploy edilebilir durumda olur
- facility stok yonetimi tamamen inventory tarafinda calisir
- klinik stok entegrasyonlari kontrollu ve secici bicimde aktiflesir
- ameliyat alani inventory ile veri sahipligini bozmadan konusur
- sistem gereksiz microservice daginikligina dusmeden iki servisli daha guclu bir yapiya evrilir
