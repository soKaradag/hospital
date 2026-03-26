-- departments tablosu, hastanedeki ana bölüm kayıtlarını tutar.
-- Bu tablo doktorların bağlı olduğu organizasyonel yapıyı temsil eder.
create table if not exists departments (
    -- UUID kimlik uygulama tarafında üretilir ve veritabanında varchar(36) olarak saklanır.
    id varchar(36) not null,
    -- Bölüm adı zorunludur.
    name varchar(100) not null,
    -- Kısa açıklama alanı opsiyoneldir.
    description varchar(255),
    -- created_at ve updated_at alanları tüm ana tablolarda ortak audit alanlarıdır.
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_departments primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
-- engine-InnoDB: Satır bazlı kilitlenme (row-level locking) sağlar, bu da yüksek eş zamanlı okuma ve yazma işlemlerinde performansı artırır.
-- charset=utf8mb4: 4 byte karakter setini destekler, bu sayede emoji gibi geniş karakter yelpazesi saklanabilir.
-- collate=utf8mb4_unicode_ci: Karakter karşılaştırmalarında büyük/küçük harf duyarsız (case-insensitive) sıralama ve filtreleme sağlar.
