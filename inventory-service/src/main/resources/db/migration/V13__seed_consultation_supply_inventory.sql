insert into inventory_items (id, category_id, code, name, description, track_batches, track_expiry, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000301',
       '00000000-0000-0000-0000-000000000203',
       'CONSULTATION',
       'Consultation Supply Kit',
       'Seeded clinical supply item consumed by default encounter procedures',
       b'1',
       b'0',
       b'1',
       now(6),
       now(6)
where not exists (select 1 from inventory_items where code = 'CONSULTATION');

insert into inventory_item_units (id, inventory_item_id, unit_code, unit_name, conversion_factor, base_unit, created_at, updated_at)
select '00000000-0000-0000-0000-000000000302',
       '00000000-0000-0000-0000-000000000301',
       'KIT',
       'Kit',
       1.0000,
       b'1',
       now(6),
       now(6)
where not exists (
    select 1 from inventory_item_units
    where inventory_item_id = '00000000-0000-0000-0000-000000000301'
      and unit_code = 'KIT'
);

insert into stock_batches (id, inventory_item_id, warehouse_id, warehouse_zone_id, batch_number, expires_at, quantity_on_hand, active, created_at, updated_at)
select '00000000-0000-0000-0000-000000000303',
       '00000000-0000-0000-0000-000000000301',
       '00000000-0000-0000-0000-000000000201',
       '00000000-0000-0000-0000-000000000202',
       'CONSULTATION-KIT-001',
       null,
       300.0000,
       b'1',
       now(6),
       now(6)
where not exists (select 1 from stock_batches where id = '00000000-0000-0000-0000-000000000303');

insert into stock_movements (id, inventory_item_id, stock_batch_id, warehouse_id, warehouse_zone_id, movement_type, quantity, occurred_at, reference_type, reference_id, notes, created_at, updated_at)
select '00000000-0000-0000-0000-000000000304',
       '00000000-0000-0000-0000-000000000301',
       '00000000-0000-0000-0000-000000000303',
       '00000000-0000-0000-0000-000000000201',
       '00000000-0000-0000-0000-000000000202',
       'INBOUND',
       300.0000,
       now(6),
       'clinical_seed',
       'consultation-bootstrap',
       'Seeded consultation supply stock for encounter procedure integration',
       now(6),
       now(6)
where not exists (select 1 from stock_movements where id = '00000000-0000-0000-0000-000000000304');
