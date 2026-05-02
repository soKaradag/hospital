create table if not exists inventory_items (
    id varchar(36) not null,
    category_id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    description varchar(255),
    track_batches bit not null,
    track_expiry bit not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_inventory_items primary key (id),
    constraint uk_inventory_items_code unique (code),
    constraint fk_inventory_items_category foreign key (category_id) references inventory_categories(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_inventory_items_category_id on inventory_items (category_id);

create table if not exists inventory_item_units (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    unit_code varchar(50) not null,
    unit_name varchar(100) not null,
    conversion_factor decimal(19,4) not null,
    base_unit bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_inventory_item_units primary key (id),
    constraint fk_inventory_item_units_item foreign key (inventory_item_id) references inventory_items(id),
    constraint uk_inventory_item_units_item_code unique (inventory_item_id, unit_code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_inventory_item_units_item_id on inventory_item_units (inventory_item_id);

create table if not exists inventory_item_aliases (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    alias varchar(150) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_inventory_item_aliases primary key (id),
    constraint fk_inventory_item_aliases_item foreign key (inventory_item_id) references inventory_items(id),
    constraint uk_inventory_item_aliases_alias unique (alias)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_inventory_item_aliases_item_id on inventory_item_aliases (inventory_item_id);

create table if not exists inventory_item_barcodes (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    barcode varchar(150) not null,
    unit_code varchar(50),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_inventory_item_barcodes primary key (id),
    constraint fk_inventory_item_barcodes_item foreign key (inventory_item_id) references inventory_items(id),
    constraint uk_inventory_item_barcodes_barcode unique (barcode)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_inventory_item_barcodes_item_id on inventory_item_barcodes (inventory_item_id);
