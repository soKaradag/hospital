-- encounters tablosu, hastanın doktorla gerçekleşen klinik temasının çekirdek kaydını tutar.
-- Sonraki fazlarda reçete, laboratuvar ve tanı detayları bu kayda bağlanabilir.
create table if not exists encounters (
    -- UUID kimlik alanı.
    id varchar(36) not null,
    -- Muayene randevuya bağlı olabilir, ancak zorunlu değildir.
    appointment_id varchar(36),
    -- Her encounter mutlaka bir hasta ve doktor ile ilişkilidir.
    patient_id varchar(36) not null,
    doctor_id varchar(36) not null,
    -- Klinik çekirdek metin alanları.
    complaint varchar(500),
    diagnosis_note varchar(1000),
    treatment_note varchar(1000),
    -- Gerçek işlem zamanı UTC kabul edilerek tutulur.
    encounter_date_time datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_encounters primary key (id),
    constraint fk_encounters_appointment foreign key (appointment_id) references appointments(id),
    constraint fk_encounters_patient foreign key (patient_id) references patients(id),
    constraint fk_encounters_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Randevu bağlantılı muayene sorguları için index.
create index idx_encounters_appointment_id on encounters (appointment_id);
-- Hasta bazlı muayene listeleri için index.
create index idx_encounters_patient_id on encounters (patient_id);
-- Doktor bazlı muayene listeleri için index.
create index idx_encounters_doctor_id on encounters (doctor_id);
-- Zaman bazlı sıralama ve filtreleme için index.
create index idx_encounters_date_time on encounters (encounter_date_time);
