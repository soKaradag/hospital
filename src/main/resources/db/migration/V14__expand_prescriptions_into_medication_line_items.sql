create table medications (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    form varchar(100),
    strength varchar(100),
    active bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint uk_medications_code unique (code),
    constraint uk_medications_name unique (name)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table prescription_items (
    id varchar(36) not null,
    prescription_id varchar(36) not null,
    medication_id varchar(36) not null,
    dosage varchar(100) not null,
    frequency varchar(100),
    duration_days int,
    instructions varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_prescription_items_prescription foreign key (prescription_id) references prescriptions (id),
    constraint fk_prescription_items_medication foreign key (medication_id) references medications (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table prescription_dispenses (
    id varchar(36) not null,
    prescription_item_id varchar(36) not null,
    dispensed_at datetime(6) not null,
    quantity int not null,
    status varchar(50) not null,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_prescription_dispenses_item foreign key (prescription_item_id) references prescription_items (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into medications (id, code, name, form, strength, active, created_at, updated_at)
values
    ('3d58b087-4cb8-42dc-9814-9f7e56b4dcb1', 'GENERAL_MED', 'General Medication', 'Tablet', '500mg', b'1', current_timestamp(6), current_timestamp(6));

create index idx_prescription_items_prescription_id on prescription_items (prescription_id);
create index idx_prescription_dispenses_item_id on prescription_dispenses (prescription_item_id);
