insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select uuid(), r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r
join permissions p on p.code = 'inventory.stock.reserve'
where r.code in ('DOCTOR', 'NURSE')
  and not exists (
      select 1
      from role_permissions existing
      where existing.role_id = r.id
        and existing.permission_id = p.id
  );
