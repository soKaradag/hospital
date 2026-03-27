-- diseases tablosu hastalık katalogunu tutar.
create table if not exists diseases (
    id varchar(36) not null,
    code varchar(50) not null,
    name varchar(150) not null,
    description varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_diseases primary key (id),
    constraint uk_diseases_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- patient_diseases tablosu hasta ile hastalık arasındaki geçmiş ilişkisini tutar.
create table if not exists patient_diseases (
    id varchar(36) not null,
    patient_id varchar(36) not null,
    disease_id varchar(36) not null,
    diagnosed_at datetime(6),
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_patient_diseases primary key (id),
    constraint fk_patient_diseases_patient foreign key (patient_id) references patients(id),
    constraint fk_patient_diseases_disease foreign key (disease_id) references diseases(id),
    constraint uk_patient_diseases_patient_disease unique (patient_id, disease_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_patient_diseases_patient_id on patient_diseases (patient_id);
create index idx_patient_diseases_disease_id on patient_diseases (disease_id);
