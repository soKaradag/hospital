insert into warehouses (id, code, name, type, description, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000101',
       'FAC-MAIN',
       'Facility Main Warehouse',
       'FACILITY',
       'Facility and housekeeping stock warehouse',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from warehouses where code = 'FAC-MAIN');

insert into warehouse_zones (id, warehouse_id, code, name, zone_type, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000102',
       '00000000-0000-0000-0000-000000000101',
       'HOUSEKEEPING',
       'Housekeeping Zone',
       'FACILITY',
       b'1',
       now(6),
       now(6)
where not exists (
    select 1
    from warehouse_zones
    where warehouse_id = '00000000-0000-0000-0000-000000000101'
      and code = 'HOUSEKEEPING'
);

insert into inventory_categories (id, code, name, description, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000103',
       'FACILITY_SUPPLIES',
       'Facility Supplies',
       'Cleaning and facility management supplies',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from inventory_categories where code = 'FACILITY_SUPPLIES');

insert into suppliers (id, code, name, contact_person, email, phone, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000104',
       'CLN-SUP',
       'CleanFlow Supply Co',
       'Aylin Demir',
       'ops@cleanflow.local',
       '+90-212-000-0001',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from suppliers where code = 'CLN-SUP');

insert into inventory_items (id, inventory_category_id, code, name, description, track_batches, track_expiry, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000105',
       '00000000-0000-0000-0000-000000000103',
       'DISINFECTANT_WIPES',
       'Disinfectant Wipes',
       'Surface cleaning wipes for facility teams',
       b'0',
       b'0',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from inventory_items where code = 'DISINFECTANT_WIPES');

insert into inventory_items (id, inventory_category_id, code, name, description, track_batches, track_expiry, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000103',
       'TRASH_BAG_55L',
       'Trash Bag 55L',
       'Waste collection bags for general facility use',
       b'0',
       b'0',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from inventory_items where code = 'TRASH_BAG_55L');

insert into inventory_item_units (id, inventory_item_id, code, name, conversion_factor, base_unit, created_at, updated_at)
select '00000000-0000-0000-0000-000000000107',
       '00000000-0000-0000-0000-000000000105',
       'PACK',
       'Pack',
       1.0000,
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from inventory_item_units where inventory_item_id = '00000000-0000-0000-0000-000000000105' and code = 'PACK'
);

insert into inventory_item_units (id, inventory_item_id, code, name, conversion_factor, base_unit, created_at, updated_at)
select '00000000-0000-0000-0000-000000000108',
       '00000000-0000-0000-0000-000000000106',
       'ROLL',
       'Roll',
       1.0000,
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from inventory_item_units where inventory_item_id = '00000000-0000-0000-0000-000000000106' and code = 'ROLL'
);

insert into supplier_catalog_items (id, supplier_id, inventory_item_id, supplier_item_code, unit_code, unit_price, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000109',
       '00000000-0000-0000-0000-000000000104',
       '00000000-0000-0000-0000-000000000105',
       'CF-WIPE-01',
       'PACK',
       8.5000,
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from supplier_catalog_items
    where supplier_id = '00000000-0000-0000-0000-000000000104'
      and inventory_item_id = '00000000-0000-0000-0000-000000000105'
      and unit_code = 'PACK'
);

insert into supplier_catalog_items (id, supplier_id, inventory_item_id, supplier_item_code, unit_code, unit_price, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000110',
       '00000000-0000-0000-0000-000000000104',
       '00000000-0000-0000-0000-000000000106',
       'CF-BAG-55',
       'ROLL',
       2.2000,
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from supplier_catalog_items
    where supplier_id = '00000000-0000-0000-0000-000000000104'
      and inventory_item_id = '00000000-0000-0000-0000-000000000106'
      and unit_code = 'ROLL'
);

insert into purchase_orders (id, supplier_id, code, status, notes, created_at, updated_at)
select '00000000-0000-0000-0000-000000000111',
       '00000000-0000-0000-0000-000000000104',
       'PO-FAC-001',
       'COMPLETED',
       'Seeded facility stock replenishment order',
       now(6),
       now(6)
where not exists (select 1 from purchase_orders where code = 'PO-FAC-001');

insert into purchase_order_items (id, purchase_order_id, inventory_item_id, supplier_catalog_item_id, unit_code, quantity, unit_price, received_quantity, created_at, updated_at)
select '00000000-0000-0000-0000-000000000112',
       '00000000-0000-0000-0000-000000000111',
       '00000000-0000-0000-0000-000000000105',
       '00000000-0000-0000-0000-000000000109',
       'PACK',
       40.0000,
       8.5000,
       40.0000,
       now(6),
       now(6)
where not exists (select 1 from purchase_order_items where id = '00000000-0000-0000-0000-000000000112');

insert into purchase_order_items (id, purchase_order_id, inventory_item_id, supplier_catalog_item_id, unit_code, quantity, unit_price, received_quantity, created_at, updated_at)
select '00000000-0000-0000-0000-000000000113',
       '00000000-0000-0000-0000-000000000111',
       '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000110',
       'ROLL',
       25.0000,
       2.2000,
       25.0000,
       now(6),
       now(6)
where not exists (select 1 from purchase_order_items where id = '00000000-0000-0000-0000-000000000113');

insert into stock_batches (id, inventory_item_id, warehouse_id, warehouse_zone_id, batch_number, expires_at, quantity_on_hand, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000114',
       '00000000-0000-0000-0000-000000000105',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       'FAC-WIPES-BOOTSTRAP',
       null,
       40.0000,
       b'1',
       now(6),
       now(6)
where not exists (select 1 from stock_batches where id = '00000000-0000-0000-0000-000000000114');

insert into stock_batches (id, inventory_item_id, warehouse_id, warehouse_zone_id, batch_number, expires_at, quantity_on_hand, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000115',
       '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       'FAC-BAGS-BOOTSTRAP',
       null,
       5.0000,
       b'1',
       now(6),
       now(6)
where not exists (select 1 from stock_batches where id = '00000000-0000-0000-0000-000000000115');

insert into goods_receipts (id, purchase_order_id, warehouse_id, warehouse_zone_id, code, notes, received_at, created_at, updated_at)
select '00000000-0000-0000-0000-000000000116',
       '00000000-0000-0000-0000-000000000111',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       'GR-FAC-001',
       'Seeded facility goods receipt',
       now(6),
       now(6),
       now(6)
where not exists (select 1 from goods_receipts where code = 'GR-FAC-001');

insert into goods_receipt_items (id, goods_receipt_id, purchase_order_item_id, inventory_item_id, stock_batch_id, batch_number, expires_at, quantity, unit_price, created_at, updated_at)
select '00000000-0000-0000-0000-000000000117',
       '00000000-0000-0000-0000-000000000116',
       '00000000-0000-0000-0000-000000000112',
       '00000000-0000-0000-0000-000000000105',
       '00000000-0000-0000-0000-000000000114',
       'FAC-WIPES-BOOTSTRAP',
       null,
       40.0000,
       8.5000,
       now(6),
       now(6)
where not exists (select 1 from goods_receipt_items where id = '00000000-0000-0000-0000-000000000117');

insert into goods_receipt_items (id, goods_receipt_id, purchase_order_item_id, inventory_item_id, stock_batch_id, batch_number, expires_at, quantity, unit_price, created_at, updated_at)
select '00000000-0000-0000-0000-000000000118',
       '00000000-0000-0000-0000-000000000116',
       '00000000-0000-0000-0000-000000000113',
       '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000115',
       'FAC-BAGS-BOOTSTRAP',
       null,
       25.0000,
       2.2000,
       now(6),
       now(6)
where not exists (select 1 from goods_receipt_items where id = '00000000-0000-0000-0000-000000000118');

insert into stock_movements (id, inventory_item_id, stock_batch_id, warehouse_id, warehouse_zone_id, movement_type, quantity, occurred_at, reference_type, reference_id, notes, created_at, updated_at)
select '00000000-0000-0000-0000-000000000119',
       '00000000-0000-0000-0000-000000000105',
       '00000000-0000-0000-0000-000000000114',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       'INBOUND',
       40.0000,
       now(6),
       'goods_receipt',
       '00000000-0000-0000-0000-000000000116',
       'Seeded facility stock inbound',
       now(6),
       now(6)
where not exists (select 1 from stock_movements where id = '00000000-0000-0000-0000-000000000119');

insert into stock_movements (id, inventory_item_id, stock_batch_id, warehouse_id, warehouse_zone_id, movement_type, quantity, occurred_at, reference_type, reference_id, notes, created_at, updated_at)
select '00000000-0000-0000-0000-000000000120',
       '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000115',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       'INBOUND',
       25.0000,
       now(6),
       'goods_receipt',
       '00000000-0000-0000-0000-000000000116',
       'Seeded facility stock inbound',
       now(6),
       now(6)
where not exists (select 1 from stock_movements where id = '00000000-0000-0000-0000-000000000120');

insert into stock_movements (id, inventory_item_id, stock_batch_id, warehouse_id, warehouse_zone_id, movement_type, quantity, occurred_at, reference_type, reference_id, notes, created_at, updated_at)
select '00000000-0000-0000-0000-000000000123',
       '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000115',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       'OUTBOUND',
       20.0000,
       now(6),
       'facility_use',
       'seed-facility-usage-001',
       'Seeded facility stock consumption to create a low-stock scenario',
       now(6),
       now(6)
where not exists (select 1 from stock_movements where id = '00000000-0000-0000-0000-000000000123');

insert into reorder_rules (id, inventory_item_id, warehouse_id, warehouse_zone_id, preferred_supplier_id, min_quantity, target_quantity, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000121',
       '00000000-0000-0000-0000-000000000105',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       '00000000-0000-0000-0000-000000000104',
       20.0000,
       60.0000,
       b'1',
       now(6),
       now(6)
where not exists (select 1 from reorder_rules where id = '00000000-0000-0000-0000-000000000121');

insert into reorder_rules (id, inventory_item_id, warehouse_id, warehouse_zone_id, preferred_supplier_id, min_quantity, target_quantity, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000122',
       '00000000-0000-0000-0000-000000000106',
       '00000000-0000-0000-0000-000000000101',
       '00000000-0000-0000-0000-000000000102',
       '00000000-0000-0000-0000-000000000104',
       10.0000,
       30.0000,
       b'1',
       now(6),
       now(6)
where not exists (select 1 from reorder_rules where id = '00000000-0000-0000-0000-000000000122');
