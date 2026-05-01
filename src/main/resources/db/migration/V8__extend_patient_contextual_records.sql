create table patient_emergency_contacts (
    id varchar(36) not null,
    patient_id varchar(36) not null,
    full_name varchar(150) not null,
    relationship varchar(100),
    phone_number varchar(30),
    is_primary bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_patient_emergency_contacts_patient foreign key (patient_id) references patients (id)
);

create table patient_insurances (
    id varchar(36) not null,
    patient_id varchar(36) not null,
    provider_name varchar(150) not null,
    policy_number varchar(100) not null,
    coverage_type varchar(100),
    active bit not null default b'1',
    effective_date date,
    expiry_date date,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_patient_insurances_patient foreign key (patient_id) references patients (id),
    constraint uk_patient_insurances_policy_number unique (policy_number)
);

create index idx_patient_emergency_contacts_patient_id on patient_emergency_contacts (patient_id);
create index idx_patient_insurances_patient_id on patient_insurances (patient_id);
