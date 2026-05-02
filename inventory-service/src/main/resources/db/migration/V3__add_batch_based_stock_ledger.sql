create table if not exists stock_batches (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    batch_number varchar(100) not null,
    expires_at date,
    quantity_on_hand decimal(19,4) not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_batches primary key (id),
    constraint fk_stock_batches_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_stock_batches_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_stock_batches_zone foreign key (warehouse_zone_id) references warehouse_zones(id),
    constraint uk_stock_batches_item_warehouse_batch unique (inventory_item_id, warehouse_id, batch_number)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_stock_batches_item_id on stock_batches (inventory_item_id);
create index idx_stock_batches_warehouse_id on stock_batches (warehouse_id);
create index idx_stock_batches_zone_id on stock_batches (warehouse_zone_id);

create table if not exists stock_movements (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    stock_batch_id varchar(36),
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    movement_type varchar(40) not null,
    quantity decimal(19,4) not null,
    occurred_at datetime(6) not null,
    reference_type varchar(80),
    reference_id varchar(80),
    notes varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_movements primary key (id),
    constraint fk_stock_movements_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_stock_movements_batch foreign key (stock_batch_id) references stock_batches(id),
    constraint fk_stock_movements_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_stock_movements_zone foreign key (warehouse_zone_id) references warehouse_zones(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_stock_movements_item_id on stock_movements (inventory_item_id);
create index idx_stock_movements_batch_id on stock_movements (stock_batch_id);
create index idx_stock_movements_occurred_at on stock_movements (occurred_at);
