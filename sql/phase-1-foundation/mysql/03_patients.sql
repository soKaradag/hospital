-- patients tablosu, hasta çekirdek kaydını tutar.
-- Contact, Address, Phone, Country ve City ayrı tablo değildir; bu tabloya gömülü kolonlar olarak yazılır.
create table if not exists patients (
    -- UUID kimlik alanı.
    id varchar(36) not null,
    -- Temel hasta kimlik bilgileri.
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    -- Ulusal kimlik bilgisi opsiyonel olabilir ama varsa tekil olmalıdır.
    national_id varchar(20),
    -- Sadece tarih gerektiren alanlar date olarak tutulur.
    birth_date date,
    -- Enum değerleri veritabanında string olarak saklanır.
    gender varchar(20),
    -- Contact -> Phone alanları.
    phone_country_code varchar(10),
    phone_number varchar(20),
    email varchar(150),
    -- Address -> Country ve City alanları.
    country_code varchar(10),
    country_name varchar(100),
    city_code varchar(20),
    city_name varchar(100),
    -- Adresin kalan serbest alanları.
    district varchar(100),
    postal_code varchar(20),
    address_line varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_patients primary key (id),
    constraint uk_patients_national_id unique (national_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
