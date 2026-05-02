create table encounter_diagnosis_history (
    id varchar(36) not null,
    encounter_diagnosis_id varchar(36) not null,
    disease_id varchar(36) not null,
    notes varchar(1000),
    revised_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_encounter_diagnosis_history_diagnosis foreign key (encounter_diagnosis_id) references encounter_diagnoses (id),
    constraint fk_encounter_diagnosis_history_disease foreign key (disease_id) references diseases (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into encounter_diagnosis_history (id, encounter_diagnosis_id, disease_id, notes, revised_at, created_at, updated_at)
select
    uuid(),
    ed.id,
    ed.disease_id,
    ed.notes,
    ed.updated_at,
    current_timestamp(6),
    current_timestamp(6)
from encounter_diagnoses ed;

create index idx_encounter_diagnosis_history_diagnosis_id on encounter_diagnosis_history (encounter_diagnosis_id);
