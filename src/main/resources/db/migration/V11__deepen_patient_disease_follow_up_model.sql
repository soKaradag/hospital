create table patient_disease_status_history (
    id varchar(36) not null,
    patient_disease_id varchar(36) not null,
    status varchar(50) not null,
    noted_at datetime(6) not null,
    note varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_patient_disease_status_history_patient_disease foreign key (patient_disease_id) references patient_diseases (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table patient_disease_followups (
    id varchar(36) not null,
    patient_disease_id varchar(36) not null,
    followup_date_time datetime(6) not null,
    status varchar(50) not null,
    note varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_patient_disease_followups_patient_disease foreign key (patient_disease_id) references patient_diseases (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into patient_disease_status_history (id, patient_disease_id, status, noted_at, note, created_at, updated_at)
select
    uuid(),
    pd.id,
    'ACTIVE',
    coalesce(pd.diagnosed_at, current_timestamp(6)),
    pd.notes,
    current_timestamp(6),
    current_timestamp(6)
from patient_diseases pd;

create index idx_patient_disease_status_history_patient_disease_id on patient_disease_status_history (patient_disease_id);
create index idx_patient_disease_followups_patient_disease_id on patient_disease_followups (patient_disease_id);
