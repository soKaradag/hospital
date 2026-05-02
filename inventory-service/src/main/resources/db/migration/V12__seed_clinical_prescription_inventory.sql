insert into warehouses (id, code, name, type, description, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000201',
       'CLINICAL-PHARMACY',
       'Clinical Pharmacy Warehouse',
       'CLINICAL',
       'Pharmacy stock used by clinical dispense workflows',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from warehouses where code = 'CLINICAL-PHARMACY');

insert into warehouse_zones (id, warehouse_id, code, name, zone_type, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000202',
       '00000000-0000-0000-0000-000000000201',
       'OUTPATIENT-DISPENSE',
       'Outpatient Dispense Zone',
       'CLINICAL',
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from warehouse_zones
    where warehouse_id = '00000000-0000-0000-0000-000000000201'
      and code = 'OUTPATIENT-DISPENSE'
);

insert into inventory_categories (id, code, name, description, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000203',
       'CLINICAL_MEDICATIONS',
       'Clinical Medications',
       'Medication stock prepared for prescription dispense workflows',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from inventory_categories where code = 'CLINICAL_MEDICATIONS');

insert into inventory_items (id, category_id, code, name, description, track_batches, track_expiry, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000204',
       '00000000-0000-0000-0000-000000000203',
       'GENERAL_MED',
       'General Medication',
       'Default seeded medication item for prescription dispense integration',
       b'1',
       b'0',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from inventory_items where code = 'GENERAL_MED');

insert into inventory_item_units (id, inventory_item_id, unit_code, unit_name, conversion_factor, base_unit, created_at, updated_at)
select '00000000-0000-0000-0000-000000000205',
       '00000000-0000-0000-0000-000000000204',
       'TABLET',
       'Tablet',
       1.0000,
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from inventory_item_units
    where inventory_item_id = '00000000-0000-0000-0000-000000000204'
      and unit_code = 'TABLET'
);

insert into stock_batches (id, inventory_item_id, warehouse_id, warehouse_zone_id, batch_number, expires_at, quantity_on_hand, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000206',
       '00000000-0000-0000-0000-000000000204',
       '00000000-0000-0000-0000-000000000201',
       '00000000-0000-0000-0000-000000000202',
       'RX-GENERAL-MED-001',
       null,
       500.0000,
       b'1',
       now(6),
       now(6)
where not exists (select 1 from stock_batches where id = '00000000-0000-0000-0000-000000000206');

insert into stock_movements (id, inventory_item_id, stock_batch_id, warehouse_id, warehouse_zone_id, movement_type, quantity, occurred_at, reference_type, reference_id, notes, created_at, updated_at)
select '00000000-0000-0000-0000-000000000207',
       '00000000-0000-0000-0000-000000000204',
       '00000000-0000-0000-0000-000000000206',
       '00000000-0000-0000-0000-000000000201',
       '00000000-0000-0000-0000-000000000202',
       'INBOUND',
       500.0000,
       now(6),
       'clinical_seed',
       'general-med-bootstrap',
       'Seeded clinical stock for prescription dispense integration',
       now(6),
       now(6)
where not exists (select 1 from stock_movements where id = '00000000-0000-0000-0000-000000000207');
