insert into permissions (id, code, name, description, created_at, updated_at)
select '2b2da6ac-7f55-46d0-9633-c289ec6a2a01', 'surgeries.read', 'Read surgeries', 'Allows reading surgery scheduling records', current_timestamp(6), current_timestamp(6)
where not exists (select 1 from permissions where code = 'surgeries.read');

insert into permissions (id, code, name, description, created_at, updated_at)
select 'c7f42b61-55fb-4700-826d-6430dfbafc02', 'surgeries.write', 'Write surgeries', 'Allows creating and updating surgery scheduling records', current_timestamp(6), current_timestamp(6)
where not exists (select 1 from permissions where code = 'surgeries.write');

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select uuid(), r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r
join permissions p on p.code in ('surgeries.read', 'surgeries.write')
where r.code in ('ADMIN', 'DOCTOR', 'NURSE')
  and not exists (
      select 1
      from role_permissions existing
      where existing.role_id = r.id
        and existing.permission_id = p.id
  );
