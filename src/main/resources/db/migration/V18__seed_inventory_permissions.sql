insert into permissions (id, code, name, description, created_at, updated_at) values
    ('f0d9ec3e-2623-46e0-9f0f-cac801f6c701', 'inventory.items.read', 'Read inventory items', 'Allows reading inventory item catalog data', current_timestamp(6), current_timestamp(6)),
    ('f5c85e92-c3d7-4f18-aad9-52cc1f747702', 'inventory.items.write', 'Write inventory items', 'Allows creating and updating inventory item catalog data', current_timestamp(6), current_timestamp(6)),
    ('4b261ff9-a5ed-4bdf-9c03-b84059577703', 'inventory.warehouses.read', 'Read inventory warehouses', 'Allows reading warehouse records', current_timestamp(6), current_timestamp(6)),
    ('ff8f69c0-ef0d-44f8-a9da-ff1df2d9fb04', 'inventory.warehouses.write', 'Write inventory warehouses', 'Allows creating and updating warehouse records', current_timestamp(6), current_timestamp(6)),
    ('ddbfb690-c41e-4c13-9730-a65ed3440905', 'inventory.suppliers.read', 'Read inventory suppliers', 'Allows reading inventory supplier records', current_timestamp(6), current_timestamp(6)),
    ('81a8700b-0a14-4e5c-b114-3b9727c35706', 'inventory.suppliers.write', 'Write inventory suppliers', 'Allows creating and updating inventory supplier records', current_timestamp(6), current_timestamp(6)),
    ('db34b7fb-f6fb-4cec-a8d5-fa5784e6a507', 'inventory.stock.read', 'Read inventory stock', 'Allows reading stock availability and movement history', current_timestamp(6), current_timestamp(6)),
    ('d6d6b5ea-3027-4752-9db3-e52db86a2308', 'inventory.stock.adjust', 'Adjust inventory stock', 'Allows manually adjusting inventory stock', current_timestamp(6), current_timestamp(6)),
    ('6474a4cf-7730-4676-9f37-89e6d58fb209', 'inventory.stock.transfer', 'Transfer inventory stock', 'Allows transferring inventory stock across warehouses and zones', current_timestamp(6), current_timestamp(6)),
    ('6475bf8a-c2f7-4bab-b87d-e9b854a0e010', 'inventory.stock.reserve', 'Reserve inventory stock', 'Allows reserving inventory stock for future clinical usage', current_timestamp(6), current_timestamp(6)),
    ('95f2e7f8-8713-41e0-bd3a-e6ef80c71711', 'inventory.stock.consume', 'Consume inventory stock', 'Allows consuming inventory stock for completed workflows', current_timestamp(6), current_timestamp(6)),
    ('ed89bf8e-96d7-41fc-b0bd-cdb3d3fd0012', 'inventory.purchase.read', 'Read inventory purchases', 'Allows reading procurement and purchase order data', current_timestamp(6), current_timestamp(6)),
    ('2da7bfda-1ecd-42c8-a4b4-793270cfe413', 'inventory.purchase.write', 'Write inventory purchases', 'Allows creating and updating purchase orders', current_timestamp(6), current_timestamp(6)),
    ('7c5ba8ea-9296-4a07-960f-6b1ca92f7214', 'inventory.receipts.write', 'Write inventory receipts', 'Allows receiving incoming inventory goods', current_timestamp(6), current_timestamp(6)),
    ('d44500f9-cab9-4a93-b8f8-c5033514f715', 'inventory.counts.manage', 'Manage inventory counts', 'Allows managing physical inventory counts', current_timestamp(6), current_timestamp(6));

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select uuid(), r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r
join permissions p on p.code like 'inventory.%'
where r.code = 'ADMIN'
  and not exists (
      select 1
      from role_permissions existing
      where existing.role_id = r.id
        and existing.permission_id = p.id
  );
