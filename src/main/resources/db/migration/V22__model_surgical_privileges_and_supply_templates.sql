create table if not exists doctor_procedure_privileges (
    id varchar(36) not null,
    doctor_id varchar(36) not null,
    procedure_code varchar(100) not null,
    procedure_name varchar(150) not null,
    active bit not null,
    granted_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_doctor_procedure_privileges primary key (id),
    constraint fk_doctor_procedure_privileges_doctor foreign key (doctor_id) references doctors(id),
    constraint uk_doctor_procedure_privileges unique (doctor_id, procedure_code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists surgery_supply_templates (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    procedure_code varchar(100) not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_surgery_supply_templates primary key (id),
    constraint uk_surgery_supply_templates_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists surgery_supply_template_items (
    id varchar(36) not null,
    surgery_supply_template_id varchar(36) not null,
    inventory_item_code varchar(100) not null,
    quantity decimal(19,4) not null,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_surgery_supply_template_items primary key (id),
    constraint fk_surgery_supply_template_items_template foreign key (surgery_supply_template_id) references surgery_supply_templates(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

alter table surgeries
    add column if not exists supply_template_id varchar(36),
    add constraint fk_surgeries_supply_template foreign key (supply_template_id) references surgery_supply_templates(id);
