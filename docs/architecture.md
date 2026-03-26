# Mimari Yaklaşım

Bu doküman projede kullanılan mimari yaklaşımı açıklar.

## Genel Yaklaşım
Proje, domain bazlı klasörleme kullanan katmanlı bir backend mimarisi ile kurulmuştur. Amaç, hem küçük başlayabilmek hem de ilerleyen fazlarda modüler monolith yapıya doğal şekilde büyüyebilmektir.

## Neden Domain Bazlı Klasörleme
Tek bir `model`, tek bir `service`, tek bir `controller` klasörü kullanmak küçük projelerde kısa vadede kolay görünür. Ancak proje büyüdüğünde hangi kodun hangi alana ait olduğu belirsizleşir. Domain bazlı yapı ise her iş alanını kendi sınırları içinde tutar.

Örnek:
- `patient` kendi entity, dto, mapper, repository, service ve controller dosyalarına sahiptir
- `doctor` kendi katmanlarına sahiptir
- bu yapı ileride modülleşmeyi kolaylaştırır

## Katmanlar

### Controller
Controller HTTP giriş noktasıdır. İstek alır, doğrular, service katmanına yönlendirir ve sonucu response formatına döner. İş kuralı controller içinde yazılmaz.

### Service
Service katmanı iş kurallarının bulunduğu yerdir. Örneğin duplicate kontrolü, ilişki doğrulaması, domain tutarlılığı ve transaction yönetimi burada yapılır.

### Repository
Repository katmanı veritabanı erişimini yönetir. Burada iş kuralı yazılmaz. Amaç sadece veri okumak ve yazmaktır.

### DTO
DTO, dış dünyaya açılan veri modelidir. Entity sınıfları doğrudan API cevabı olarak dönülmez.

### Mapper
Mapper sınıfları entity ile DTO arasındaki dönüşümü yapar. Bu projede mapper'lar manuel yazılmıştır.

### Model
Model katmanında JPA entity ve ortak value object sınıfları bulunur.

## Neden Entity Doğrudan Dönülmüyor
- Lazy loading sorunları oluşabilir
- Gereksiz alanlar dışarı açılabilir
- API sözleşmesi entity yapısına bağımlı hale gelir
- İleride entity değişince API bozulabilir

DTO kullanımı bu problemleri azaltır.

## Neden Service Katmanı Var
Service katmanı olmadan controller doğrudan repository çağırırsa iş kuralları dağılmaya başlar. Bir noktadan sonra aynı kural farklı controller'larda tekrar eder. Service katmanı iş mantığını tek yerde toplar.

## Fazlar Arası Genişleme Mantığı
Bu mimari Faz 1 için kurulmuş olsa da yalnızca Faz 1 düşünülerek tasarlanmamıştır. Amaç:
- Faz 2'de auth eklemek
- Faz 3'te modülleri büyütmek
- bunu yaparken mevcut yapıyı kırmamak

Bu nedenle bugünkü klasör yapısı ileride de korunacaktır; sadece içerik genişleyecektir.
