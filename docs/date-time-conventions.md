# Tarih ve Saat Kuralları

Bu doküman, backend ile veritabanı arasında tarih ve saat kaynaklı veri hatalarını önlemek için uygulanacak kuralları tanımlar.

## Temel Karar
- Sistem genelinde tek zaman standardı UTC'dir.
- Kalıcı veri katmanında yerel saat mantığı kullanılmaz.
- Saat dilimi dönüşümü gerekiyorsa sadece kullanıcıya gösterim sırasında yapılır.

## Hangi Tip Ne Zaman Kullanılır

### LocalDate
- Sadece takvim tarihi gereken alanlarda kullanılır.
- Örnek:
  - doğum tarihi
  - izin başlangıç tarihi
  - rapor tarihi

### Instant
- Gerçek bir anı temsil eden alanlarda `Instant` kullanılır.
- Örnek:
  - oluşturulma zamanı
  - güncellenme zamanı
  - ödeme zamanı
  - randevu zamanı
  - muayene zamanı

## Backend Kuralları
- Tarih ve saat alanı eklenirken önce bunun tarih mi yoksa gerçek zaman noktası mı olduğu netleştirilir.
- Tarih için saat bilgisi eklenmez.
- Saat bilgisi gereken alanlarda `Instant` kullanılır.
- Servis katmanında sistem saatine göre farklı yorumlar üretilmez.
- Testlerde tarih/saat davranışı deterministik olacak şekilde tasarlanır.

## Veritabanı Kuralları
- Veritabanına yazılan tüm zaman noktaları UTC kabul edilir.
- Veritabanı ve backend arasında saat dilimi farkından kaynaklı otomatik kaymalar kabul edilmez.
- SQL tarafında tarih ve saat kolonları seçilirken MySQL ve MSSQL uyumluluğu dikkate alınır.
- Kolon isimleri açık olmalıdır:
  - `created_at`
  - `updated_at`
  - `appointment_date_time`
  - `encounter_date_time`
  - `paid_at`

## API Kuralları
- API request ve response tarafında tarih/saat formatı tutarlı olmalıdır.
- Frontend veya istemci UTC ile çalışmıyorsa dönüşüm istemci katmanında yapılmalıdır.
- Backend içinde bölgesel saat varsayımıyla veri saklanmaz.

## Kaçınılacak Hatalar
- Sunucu saat dilimine güvenmek
- Veritabanı bağlantısının kendi saat dilimi yorumuna bırakmak
- Aynı projede bazı alanları yerel saat, bazı alanları UTC tutmak
- Doğum tarihi gibi alanlarda gereksiz saat bilgisi saklamak
- Zaman noktası gereken alanı sadece string gibi taşımak
