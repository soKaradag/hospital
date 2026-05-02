-- payment_audit tablosu, trigger ile oluşan finansal iz kayıtlarını saklar.
-- Amaç payments tablosundaki insert işlemlerini ayrı bir history yapısında gözlemleyebilmektir.
create table if not exists payment_audit (
    id varchar(36) not null,
    payment_id varchar(36) not null,
    patient_id varchar(36) not null,
    encounter_id varchar(36) not null,
    action varchar(20) not null,
    amount decimal(12,2) not null,
    currency varchar(3) not null,
    payment_method varchar(20) not null,
    payment_status varchar(20) not null,
    paid_at datetime(6),
    logged_at datetime(6) not null,
    constraint pk_payment_audit primary key (id),
    constraint fk_payment_audit_payment foreign key (payment_id) references payments(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_payment_audit_payment_id on payment_audit (payment_id);
create index idx_payment_audit_patient_id on payment_audit (patient_id);
create index idx_payment_audit_logged_at on payment_audit (logged_at);
