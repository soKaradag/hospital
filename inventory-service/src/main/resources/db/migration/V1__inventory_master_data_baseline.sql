create table if not exists warehouses (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    type varchar(80) not null,
    description varchar(255),
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_warehouses primary key (id),
    constraint uk_warehouses_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists warehouse_zones (
    id varchar(36) not null,
    warehouse_id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    zone_type varchar(80) not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_warehouse_zones primary key (id),
    constraint fk_warehouse_zones_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint uk_warehouse_zones_warehouse_code unique (warehouse_id, code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_warehouse_zones_warehouse_id on warehouse_zones (warehouse_id);

create table if not exists inventory_categories (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    description varchar(255),
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_inventory_categories primary key (id),
    constraint uk_inventory_categories_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists suppliers (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    contact_person varchar(120),
    email varchar(150),
    phone varchar(40),
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_suppliers primary key (id),
    constraint uk_suppliers_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
