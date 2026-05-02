create table if not exists goods_receipts (
    id varchar(36) not null,
    purchase_order_id varchar(36) not null,
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    code varchar(100) not null,
    notes varchar(255),
    received_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_goods_receipts primary key (id),
    constraint fk_goods_receipts_purchase_order foreign key (purchase_order_id) references purchase_orders(id),
    constraint fk_goods_receipts_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_goods_receipts_warehouse_zone foreign key (warehouse_zone_id) references warehouse_zones(id),
    constraint uk_goods_receipts_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists goods_receipt_items (
    id varchar(36) not null,
    goods_receipt_id varchar(36) not null,
    purchase_order_item_id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    stock_batch_id varchar(36) not null,
    batch_number varchar(100) not null,
    expires_at date,
    quantity decimal(19,4) not null,
    unit_price decimal(19,4) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_goods_receipt_items primary key (id),
    constraint fk_goods_receipt_items_receipt foreign key (goods_receipt_id) references goods_receipts(id),
    constraint fk_goods_receipt_items_purchase_order_item foreign key (purchase_order_item_id) references purchase_order_items(id),
    constraint fk_goods_receipt_items_inventory_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_goods_receipt_items_stock_batch foreign key (stock_batch_id) references stock_batches(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_goods_receipt_items_receipt_id on goods_receipt_items (goods_receipt_id);
