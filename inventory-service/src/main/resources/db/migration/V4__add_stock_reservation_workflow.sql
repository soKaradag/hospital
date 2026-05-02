create table if not exists stock_reservations (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    stock_batch_id varchar(36),
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    quantity decimal(19,4) not null,
    status varchar(40) not null,
    reservation_type varchar(80) not null,
    reference_type varchar(80),
    reference_id varchar(80),
    expires_at datetime(6),
    notes varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_reservations primary key (id),
    constraint fk_stock_reservations_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_stock_reservations_batch foreign key (stock_batch_id) references stock_batches(id),
    constraint fk_stock_reservations_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_stock_reservations_zone foreign key (warehouse_zone_id) references warehouse_zones(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_stock_reservations_item_id on stock_reservations (inventory_item_id);
create index idx_stock_reservations_batch_id on stock_reservations (stock_batch_id);
create index idx_stock_reservations_status on stock_reservations (status);
