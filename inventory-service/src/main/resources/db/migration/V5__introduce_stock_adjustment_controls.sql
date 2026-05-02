create table if not exists stock_adjustments (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    stock_batch_id varchar(36) not null,
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    quantity_delta decimal(19,4) not null,
    reason_code varchar(80) not null,
    notes varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_adjustments primary key (id),
    constraint fk_stock_adjustments_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_stock_adjustments_batch foreign key (stock_batch_id) references stock_batches(id),
    constraint fk_stock_adjustments_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_stock_adjustments_zone foreign key (warehouse_zone_id) references warehouse_zones(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_stock_adjustments_item_id on stock_adjustments (inventory_item_id);
create index idx_stock_adjustments_batch_id on stock_adjustments (stock_batch_id);
