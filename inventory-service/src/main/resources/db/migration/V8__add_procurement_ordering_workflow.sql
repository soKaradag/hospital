create table if not exists supplier_catalog_items (
    id varchar(36) not null,
    supplier_id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    supplier_item_code varchar(100) not null,
    unit_code varchar(50) not null,
    unit_price decimal(19,4) not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_supplier_catalog_items primary key (id),
    constraint fk_supplier_catalog_items_supplier foreign key (supplier_id) references suppliers(id),
    constraint fk_supplier_catalog_items_item foreign key (inventory_item_id) references inventory_items(id),
    constraint uk_supplier_catalog_item unique (supplier_id, inventory_item_id, unit_code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists purchase_orders (
    id varchar(36) not null,
    supplier_id varchar(36) not null,
    code varchar(100) not null,
    status varchar(40) not null,
    notes varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_purchase_orders primary key (id),
    constraint fk_purchase_orders_supplier foreign key (supplier_id) references suppliers(id),
    constraint uk_purchase_orders_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists purchase_order_items (
    id varchar(36) not null,
    purchase_order_id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    supplier_catalog_item_id varchar(36),
    unit_code varchar(50) not null,
    quantity decimal(19,4) not null,
    unit_price decimal(19,4) not null,
    received_quantity decimal(19,4) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_purchase_order_items primary key (id),
    constraint fk_purchase_order_items_order foreign key (purchase_order_id) references purchase_orders(id),
    constraint fk_purchase_order_items_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_purchase_order_items_catalog foreign key (supplier_catalog_item_id) references supplier_catalog_items(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_purchase_order_items_order_id on purchase_order_items (purchase_order_id);
