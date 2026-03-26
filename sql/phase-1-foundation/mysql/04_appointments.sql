-- appointments tablosu, hasta ile doktor arasındaki planlanmış randevu kaydını tutar.
create table if not exists appointments (
    -- UUID kimlik alanı.
    id varchar(36) not null,
    -- Randevunun sahibi olan hasta ve doktor foreign key ile bağlanır.
    patient_id varchar(36) not null,
    doctor_id varchar(36) not null,
    -- Gerçek zaman noktası UTC kabul edilerek datetime(6) ile tutulur.
    appointment_date_time datetime(6) not null,
    -- Enum değerleri string olarak saklanır.
    status varchar(30) not null,
    -- Randevuya ilişkin kısa operasyonel not alanı.
    notes varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_appointments primary key (id),
    constraint fk_appointments_patient foreign key (patient_id) references patients(id),
    constraint fk_appointments_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Hasta bazlı erişim ve join için index.
create index idx_appointments_patient_id on appointments (patient_id);
-- Doktor bazlı erişim ve join için index.
create index idx_appointments_doctor_id on appointments (doctor_id);
-- Zaman bazlı sıralama ve filtreleme için index.
create index idx_appointments_date_time on appointments (appointment_date_time);
