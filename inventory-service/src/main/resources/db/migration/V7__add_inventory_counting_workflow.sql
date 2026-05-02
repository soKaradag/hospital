create table if not exists stock_counts (
    id varchar(36) not null,
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    status varchar(40) not null,
    notes varchar(255),
    closed_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_counts primary key (id),
    constraint fk_stock_counts_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_stock_counts_zone foreign key (warehouse_zone_id) references warehouse_zones(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists stock_count_lines (
    id varchar(36) not null,
    stock_count_id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    stock_batch_id varchar(36) not null,
    expected_quantity decimal(19,4) not null,
    counted_quantity decimal(19,4) not null,
    difference_quantity decimal(19,4) not null,
    notes varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_count_lines primary key (id),
    constraint fk_stock_count_lines_count foreign key (stock_count_id) references stock_counts(id),
    constraint fk_stock_count_lines_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_stock_count_lines_batch foreign key (stock_batch_id) references stock_batches(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_stock_count_lines_count_id on stock_count_lines (stock_count_id);
