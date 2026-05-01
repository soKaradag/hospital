create table encounter_vitals (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    vital_type varchar(50) not null,
    vital_value varchar(100) not null,
    measured_at datetime(6) not null,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_encounter_vitals_encounter foreign key (encounter_id) references encounters (id)
);

create table encounter_procedures (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    procedure_code varchar(100) not null,
    procedure_name varchar(150) not null,
    performed_at datetime(6) not null,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_encounter_procedures_encounter foreign key (encounter_id) references encounters (id)
);

insert into encounter_procedures (id, encounter_id, procedure_code, procedure_name, performed_at, note, created_at, updated_at)
select
    uuid(),
    e.id,
    'CONSULTATION',
    'Consultation',
    e.encounter_date_time,
    e.treatment_note,
    current_timestamp(6),
    current_timestamp(6)
from encounters e
where e.treatment_note is not null
  and trim(e.treatment_note) <> '';

create index idx_encounter_vitals_encounter_id on encounter_vitals (encounter_id);
create index idx_encounter_procedures_encounter_id on encounter_procedures (encounter_id);
