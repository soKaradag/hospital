-- encounter_diagnoses tablosu muayene bazlı teşhis kayıtlarını tutar.
create table if not exists encounter_diagnoses (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    disease_id varchar(36) not null,
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_encounter_diagnoses primary key (id),
    constraint fk_encounter_diagnoses_encounter foreign key (encounter_id) references encounters(id),
    constraint fk_encounter_diagnoses_disease foreign key (disease_id) references diseases(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_encounter_diagnoses_encounter_id on encounter_diagnoses (encounter_id);
create index idx_encounter_diagnoses_disease_id on encounter_diagnoses (disease_id);

-- prescriptions tablosu encounter sonrası oluşan reçete üst kaydını tutar.
create table if not exists prescriptions (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    patient_id varchar(36) not null,
    doctor_id varchar(36) not null,
    prescription_date date not null,
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_prescriptions primary key (id),
    constraint fk_prescriptions_encounter foreign key (encounter_id) references encounters(id),
    constraint fk_prescriptions_patient foreign key (patient_id) references patients(id),
    constraint fk_prescriptions_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_prescriptions_encounter_id on prescriptions (encounter_id);
create index idx_prescriptions_patient_id on prescriptions (patient_id);
create index idx_prescriptions_doctor_id on prescriptions (doctor_id);
create index idx_prescriptions_prescription_date on prescriptions (prescription_date);
