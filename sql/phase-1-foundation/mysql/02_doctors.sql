-- doctors tablosu, doktor kimliği, iletişim bilgisi ve bağlı olduğu bölümü tutar.
create table if not exists doctors (
    -- UUID kimlik alanı.
    id varchar(36) not null,
    -- Temel kimlik alanları.
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    -- Uzmanlık bilgisi bölümden ayrı tutulur; ileride detaylandırılabilir.
    specialization varchar(100),
    -- Phone value object içindeki alanlar ayrı kolonlara açılır.
    phone_country_code varchar(10),
    phone_number varchar(20),
    -- E-posta Contact value object içinden gelir.
    email varchar(150),
    -- Her doktor bir bölüme bağlı olmak zorundadır.
    department_id varchar(36) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_doctors primary key (id),
    -- doctors.department_id -> departments.id ilişkisi.
    constraint fk_doctors_department foreign key (department_id) references departments(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Bölüm bazlı filtreleme ve join performansı için index.
create index idx_doctors_department_id on doctors (department_id);
