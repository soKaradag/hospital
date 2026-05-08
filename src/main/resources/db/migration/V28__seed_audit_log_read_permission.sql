insert into permissions (id, code, name, description, created_at, updated_at)
select 'a6bc72bb-93b7-4ca3-8f42-5734871a2801',
       'audit-logs.read',
       'Read audit logs',
       'Allows reading audit log records and details',
       current_timestamp(6),
       current_timestamp(6)
where not exists (
    select 1
    from permissions
    where code = 'audit-logs.read'
);

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select uuid(), r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r
join permissions p on p.code = 'audit-logs.read'
where r.code = 'ADMIN'
  and not exists (
      select 1
      from role_permissions existing
      where existing.role_id = r.id
        and existing.permission_id = p.id
  );
