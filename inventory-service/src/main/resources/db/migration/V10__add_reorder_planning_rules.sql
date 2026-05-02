create table if not exists reorder_rules (
    id varchar(36) not null,
    inventory_item_id varchar(36) not null,
    warehouse_id varchar(36) not null,
    warehouse_zone_id varchar(36),
    preferred_supplier_id varchar(36),
    min_quantity decimal(19,4) not null,
    target_quantity decimal(19,4) not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_reorder_rules primary key (id),
    constraint fk_reorder_rules_item foreign key (inventory_item_id) references inventory_items(id),
    constraint fk_reorder_rules_warehouse foreign key (warehouse_id) references warehouses(id),
    constraint fk_reorder_rules_warehouse_zone foreign key (warehouse_zone_id) references warehouse_zones(id),
    constraint fk_reorder_rules_supplier foreign key (preferred_supplier_id) references suppliers(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_reorder_rules_item_location on reorder_rules (inventory_item_id, warehouse_id, warehouse_zone_id);
