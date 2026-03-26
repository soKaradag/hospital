-- payments tablosu, encounter ile ilişkili finansal hareketleri tutar.
create table if not exists payments (
    -- UUID kimlik alanı.
    id varchar(36) not null,
    -- Ödeme kaydı hem hasta hem encounter ile ilişkilidir.
    patient_id varchar(36) not null,
    encounter_id varchar(36) not null,
    -- Tutar finansal doğruluk için decimal olarak tutulur.
    amount decimal(12,2) not null,
    -- Para birimi ve durum alanları enum değerlerinin string karşılığıdır.
    currency varchar(3) not null,
    payment_method varchar(20) not null,
    payment_status varchar(20) not null,
    -- Gerçek tahsilat zamanı varsa UTC kabul edilerek yazılır.
    paid_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_payments primary key (id),
    constraint fk_payments_patient foreign key (patient_id) references patients(id),
    constraint fk_payments_encounter foreign key (encounter_id) references encounters(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Hasta bazlı ödeme listeleri için index.
create index idx_payments_patient_id on payments (patient_id);
-- Encounter bazlı finansal takip için index.
create index idx_payments_encounter_id on payments (encounter_id);
-- Tahsilat zamanına göre sorgular için index.
create index idx_payments_paid_at on payments (paid_at);
