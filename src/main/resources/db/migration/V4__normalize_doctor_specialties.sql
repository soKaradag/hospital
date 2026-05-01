create table specialties (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(100) not null,
    description varchar(255),
    active bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint uk_specialties_code unique (code),
    constraint uk_specialties_name unique (name)
);

create table doctor_specialties (
    id varchar(36) not null,
    doctor_id varchar(36) not null,
    specialty_id varchar(36) not null,
    is_primary bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_doctor_specialties_doctor foreign key (doctor_id) references doctors (id),
    constraint fk_doctor_specialties_specialty foreign key (specialty_id) references specialties (id),
    constraint uk_doctor_specialties_doctor_specialty unique (doctor_id, specialty_id)
);

insert into specialties (id, code, name, description, active, created_at, updated_at)
select
    uuid(),
    upper(replace(trim(specialization), ' ', '_')),
    trim(specialization),
    concat(trim(specialization), ' specialty'),
    b'1',
    current_timestamp(6),
    current_timestamp(6)
from doctors
where specialization is not null
  and trim(specialization) <> ''
group by trim(specialization);

insert into doctor_specialties (id, doctor_id, specialty_id, is_primary, created_at, updated_at)
select
    uuid(),
    d.id,
    s.id,
    b'1',
    current_timestamp(6),
    current_timestamp(6)
from doctors d
join specialties s on s.name = trim(d.specialization)
where d.specialization is not null
  and trim(d.specialization) <> '';
