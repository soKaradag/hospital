# Faz 4 - Inventory Service Tasarimi

Bu dokuman, mevcut `hospital-core` yapisini bozmadan yanina eklenecek `inventory-service` icin Faz 4 tasarimini tanimlar.

Amac, klinik ve operasyonel cekirdegi parcalamadan stok yonetimi, depo hareketleri, batch takibi ve tedarik akislarini ayri bir bounded context olarak kurmaktir.

## Hedef

- Mevcut servis: `hospital-core`
- Yeni servis: `inventory-service`
- Yaklasim: iki servisli yapi
- Mevcut `hospital-core` tablo sayisi: `48`
- `inventory-service` hedef tablo sayisi: `22`
- Ekosistem toplam tablo sayisi: `70`

## Temel Karar

- `hospital-core` mevcut haliyle korunur.
- Faz 4 kapsaminda klinik domainler microservice'lere bolunmez.
- Yeni servis yalnizca envanter ve tedarik akislarindan sorumlu olur.
- `inventory-service` once net API sinirlariyla kurulur; gereksiz daginik event mimarisine erken gecilmez.
- Servisler arasi bag dogrudan tablo paylasimi ile degil, HTTP API ve sonradan eklenebilecek domain event'leri ile kurulur.

## Neden Ayri Inventory Service

- Stok, batch, SKT, depo, tedarik ve transfer kurallari klinik cekirdekten farkli bir is alani olusturur.
- Envanter akislarinin degisim hizi genelde hasta ve encounter akislarindan farklidir.
- Klinik veriyi bozmadan inventory alanini bagimsiz gelistirmek daha dusuk risklidir.
- Ilerde finance de ayrilmak isterse benzer bir servis ayrisma modeli tekrar kullanilabilir.

## Servis Siniri

### Hospital Core

Asagidaki alanlar `hospital-core` icinde kalir:
- auth ve permission-first RBAC
- doctor, patient, appointment
- encounter ve encounter diagnosis
- prescription ve prescription dispenses
- payment, invoice, reporting

`hospital-core`, fiziksel stok kaydinin sahibi olmaz. Yalnizca klinik ihtiyac ve tuketim niyetini ifade eder.

### Inventory Service

Asagidaki alanlar `inventory-service` icinde olur:
- depo ve alt bolge yonetimi
- stok kalemleri ve birim tanimlari
- batch, lot ve son kullanma tarihi takibi
- stok giris, cikis, tuketim, iade ve duzeltme hareketleri
- rezervasyon ve transfer akisleri
- satin alma ve mal kabul akislari
- yeniden siparis kurallari

## Inventory Service Tablo Tasarimi

### Master Data

1. `warehouses`
2. `warehouse_zones`
3. `inventory_categories`
4. `inventory_items`
5. `inventory_item_units`
6. `inventory_item_aliases`
7. `inventory_item_barcodes`
8. `suppliers`

### Stock Operations

9. `stock_batches`
10. `stock_movements`
11. `stock_reservations`
12. `stock_adjustments`
13. `stock_transfer_requests`
14. `stock_transfers`
15. `stock_counts`
16. `stock_count_lines`

### Procurement

17. `purchase_orders`
18. `purchase_order_items`
19. `goods_receipts`
20. `goods_receipt_items`
21. `supplier_catalog_items`

### Governance

22. `reorder_rules`

## Tablo Gruplari ve Anlamlari

### Depo Yapisi

- `warehouses`: merkez depo, eczane deposu, ameliyathane deposu gibi ana stok lokasyonlari
- `warehouse_zones`: raf, oda, dolap, cabinet veya ic bolge seviyesinde alt lokasyonlar

### Urun ve Tanim Katmani

- `inventory_categories`: ilac, sarf, cihaz parcasi, laboratuvar malzemesi gibi gruplar
- `inventory_items`: stokta izlenen ana kalem
- `inventory_item_units`: kutu, adet, ml, tablet gibi birim cevrimleri
- `inventory_item_aliases`: farkli ekiplerin ayni urunu baska isimle arayabilmesi
- `inventory_item_barcodes`: barkod ile hizli okuma ve mal kabul kolayligi

### Tedarik Katmani

- `suppliers`: tedarikci kartlari
- `supplier_catalog_items`: tedarikci bazli urun kodu, birim ve fiyat eslemeleri
- `purchase_orders`: satin alma basligi
- `purchase_order_items`: satin alma satirlari
- `goods_receipts`: mal kabul basligi
- `goods_receipt_items`: teslim alinan satirlar, batch ve miktar bilgisi

### Stok Operasyonlari

- `stock_batches`: lot ve SKT bazli fiziksel stok
- `stock_movements`: giris, cikis, iade, fire, tuketim, transfer gibi tum hareketlerin ledger kaydi
- `stock_reservations`: klinik kullanim icin ayrilan ama henuz tuketilmeyen stok
- `stock_adjustments`: manuel duzeltme ve sayim farki kaydi
- `stock_transfer_requests`: bir depo veya birimden digerine transfer talebi
- `stock_transfers`: onaylanmis ve uygulanmis transfer kaydi
- `stock_counts`: periyodik sayim oturumu
- `stock_count_lines`: sayim satirlari ve fark bilgileri

### Yonetisim ve Kurallar

- `reorder_rules`: min stok, hedef stok, tekrar siparis esigi ve onerilen siparis miktari

## Ana Iliskiler

- `warehouses` -> `warehouse_zones`
- `inventory_categories` -> `inventory_items`
- `inventory_items` -> `inventory_item_units`
- `inventory_items` -> `inventory_item_aliases`
- `inventory_items` -> `inventory_item_barcodes`
- `inventory_items` + `warehouse_zones` -> `stock_batches`
- `stock_batches` -> `stock_movements`
- `inventory_items` + talep baglami -> `stock_reservations`
- `stock_counts` -> `stock_count_lines`
- `suppliers` -> `supplier_catalog_items`
- `suppliers` -> `purchase_orders`
- `purchase_orders` -> `purchase_order_items`
- `goods_receipts` -> `goods_receipt_items`

## Hospital Core ile Entegrasyon

### Gonderilecek Is Sinyalleri

`hospital-core` su ihtiyaclari `inventory-service`e bildirir:
- recete dispense edildiginde stok dusum talebi
- encounter procedure veya klinik kullanim olustugunda sarf tuketim talebi
- ileri tarihli klinik uygulama varsa rezervasyon talebi

### Inventory Service Cevabi

`inventory-service` su cevaplari uretir:
- rezervasyon olusturuldu
- stok yeterli / yetersiz
- batch secimi yapildi
- stok hareketi basariyla yazildi
- transfer veya mal kabul tamamlandi

### Veri Sahipligi

- `hospital-core` klinik olaylarin sahibidir
- `inventory-service` fiziksel stok gerceginin sahibidir
- ayni hareket iki sistemde farkli amaclarla tutulabilir
- tek veri kaynagi ilkesi korunur; stok miktarinin dogrusu yalnizca `inventory-service`tedir

## API Sinirlari

### Ilk Asama Senkron API'ler

- `POST /api/inventory/reservations`
- `POST /api/inventory/consumptions`
- `POST /api/inventory/receipts`
- `POST /api/inventory/transfers`
- `GET /api/inventory/items/{itemId}/availability`
- `GET /api/inventory/items/{itemId}/movements`

### Ilk Asama Yonetim API'leri

- `POST /api/inventory/items`
- `POST /api/inventory/warehouses`
- `POST /api/inventory/suppliers`
- `POST /api/inventory/purchase-orders`
- `POST /api/inventory/counts`

Not:
- Faz 4 baslangicinda event bus zorunlu degildir.
- Ilk surum sync HTTP ile ilerleyebilir.
- Ileride outbox tabanli event akisi eklenebilir.

## Permission Yaklasimi

`inventory-service`, Faz 3'te benimsenen permission-first RBAC cizgisini korur.

Onerilen permission ornekleri:
- `inventory.items.read`
- `inventory.items.write`
- `inventory.stock.read`
- `inventory.stock.adjust`
- `inventory.stock.transfer`
- `inventory.purchase.read`
- `inventory.purchase.write`
- `inventory.counts.manage`

Bu servis kendi auth katmanini sifirdan uretmek yerine iki yaklasimdan biriyle calisabilir:
- merkezi JWT dogrulama ve servis ici permission kontrolu
- veya API gateway arkasinda token dogrulama + servis ici permission yorumlama

## Faz 4 Uygulama Sirasi

### Bolum 1 - Servis Iskeleti

1. `inventory-service` proje iskeletini olustur
2. ortak API kurallarini ve response formatini sabitle
3. Flyway migration temelini kur
4. master data tablolarini ekle

### Bolum 2 - Operasyonel Stok Akislari

1. `stock_batches` ve `stock_movements`
2. `stock_reservations`
3. `stock_adjustments`
4. transfer ve sayim yapilari

### Bolum 3 - Tedarik ve Entegrasyon

1. satin alma ve mal kabul tablolari
2. `reorder_rules`
3. `hospital-core` ile consumption ve reservation entegrasyonu
4. stok yeterlilik ve hareket raporlari

## Non-Goals

Faz 4'te su konular hedeflenmez:
- tum mevcut `hospital-core`u microservice'lere bolmek
- dagitik transaction veya saga orkestrasyonu kurmak
- inventory ile finance'i ayni anda ayirmak
- cihaz bakim yonetimi gibi farkli operasyon alanlarini inventory kapsaminda zorla eritmek

## Beklenen Sonuc

Faz 4 sonunda:
- mevcut `hospital-core` bozulmadan calisir
- ayri bir `inventory-service` bounded context'i olusur
- stok ve tedarik akislarinin veri sahipligi netlesir
- klinik kullanim ile fiziksel stok birbirinden saglikli bicimde ayrisir
- sistem iki servisli ama yonetilebilir sadelikte kalir
