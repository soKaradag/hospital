create table disease_categories (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    description varchar(255),
    active bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint uk_disease_categories_code unique (code),
    constraint uk_disease_categories_name unique (name)
);

insert into disease_categories (id, code, name, description, active, created_at, updated_at)
values
    ('9f1969a0-7240-4bbf-86c0-3f2f7008b110', 'GENERAL', 'General', 'Default disease category', b'1', current_timestamp(6), current_timestamp(6));

alter table diseases
    add column category_id varchar(36) null;

update diseases
set category_id = '9f1969a0-7240-4bbf-86c0-3f2f7008b110'
where category_id is null;

alter table diseases
    modify category_id varchar(36) not null;

alter table diseases
    add constraint fk_diseases_category foreign key (category_id) references disease_categories (id);

create index idx_diseases_category_id on diseases (category_id);

create table disease_code_mappings (
    id varchar(36) not null,
    disease_id varchar(36) not null,
    coding_system varchar(100) not null,
    external_code varchar(100) not null,
    description varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_disease_code_mappings_disease foreign key (disease_id) references diseases (id),
    constraint uk_disease_code_mappings_system_code unique (coding_system, external_code),
    constraint uk_disease_code_mappings_disease_system unique (disease_id, coding_system)
);

insert into disease_code_mappings (id, disease_id, coding_system, external_code, description, created_at, updated_at)
select
    uuid(),
    d.id,
    'INTERNAL',
    d.code,
    concat(d.name, ' internal mapping'),
    current_timestamp(6),
    current_timestamp(6)
from diseases d;

create index idx_disease_code_mappings_disease_id on disease_code_mappings (disease_id);
