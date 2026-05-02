create table if not exists stock_transfer_requests (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    stock_batch_id varchar(36) not null,
    from_warehouse_id varchar(36) not null,
    from_warehouse_zone_id varchar(36),
    to_warehouse_id varchar(36) not null,
    to_warehouse_zone_id varchar(36),
    quantity decimal(19,4) not null,
    status varchar(40) not null,
    notes varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_transfer_requests primary key (id),
    constraint fk_stock_transfer_requests_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_stock_transfer_requests_batch foreign key (stock_batch_id) references stock_batches(id),
    constraint fk_stock_transfer_requests_from_warehouse foreign key (from_warehouse_id) references warehouses(id),
    constraint fk_stock_transfer_requests_from_zone foreign key (from_warehouse_zone_id) references warehouse_zones(id),
    constraint fk_stock_transfer_requests_to_warehouse foreign key (to_warehouse_id) references warehouses(id),
    constraint fk_stock_transfer_requests_to_zone foreign key (to_warehouse_zone_id) references warehouse_zones(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_stock_transfer_requests_item_id on stock_transfer_requests (inventory_item_id);

create table if not exists stock_transfers (
    id varchar(36) not null,
    transfer_request_id varchar(36) not null,
    source_batch_id varchar(36) not null,
    destination_batch_id varchar(36) not null,
    quantity decimal(19,4) not null,
    completed_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_stock_transfers primary key (id),
    constraint fk_stock_transfers_request foreign key (transfer_request_id) references stock_transfer_requests(id),
    constraint fk_stock_transfers_source_batch foreign key (source_batch_id) references stock_batches(id),
    constraint fk_stock_transfers_destination_batch foreign key (destination_batch_id) references stock_batches(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
