insert into permissions (id, code, name, description, created_at, updated_at)
select '55c34738-1a0e-4d2b-9b4c-a8e6a9f5d101',
       'access-control.roles.read',
       'Read access control roles',
       'Allows reading access control roles',
       current_timestamp(6),
       current_timestamp(6)
where not exists (
    select 1
    from permissions
    where code = 'access-control.roles.read'
);

insert into permissions (id, code, name, description, created_at, updated_at)
select '90bd7d23-3ca4-4f85-a801-f13c8e390102',
       'access-control.roles.write',
       'Write access control roles',
       'Allows creating and updating access control roles',
       current_timestamp(6),
       current_timestamp(6)
where not exists (
    select 1
    from permissions
    where code = 'access-control.roles.write'
);

insert into permissions (id, code, name, description, created_at, updated_at)
select 'df858c6e-a072-4a53-b89d-57ef9b58f103',
       'access-control.permissions.read',
       'Read access control permissions',
       'Allows reading the access control permission catalog',
       current_timestamp(6),
       current_timestamp(6)
where not exists (
    select 1
    from permissions
    where code = 'access-control.permissions.read'
);

insert into permissions (id, code, name, description, created_at, updated_at)
select 'c417613c-0e84-48db-88df-4696130af104',
       'access-control.users.read',
       'Read access control users',
       'Allows reading access control users',
       current_timestamp(6),
       current_timestamp(6)
where not exists (
    select 1
    from permissions
    where code = 'access-control.users.read'
);

insert into permissions (id, code, name, description, created_at, updated_at)
select 'bc0b729d-fffe-4240-bfe5-65133a9ef105',
       'access-control.users.write',
       'Write access control users',
       'Allows creating and updating access control users',
       current_timestamp(6),
       current_timestamp(6)
where not exists (
    select 1
    from permissions
    where code = 'access-control.users.write'
);

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select uuid(), r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r
join permissions p on p.code in (
    'access-control.roles.read',
    'access-control.roles.write',
    'access-control.permissions.read',
    'access-control.users.read',
    'access-control.users.write'
)
where r.code = 'ADMIN'
  and not exists (
      select 1
      from role_permissions existing
      where existing.role_id = r.id
        and existing.permission_id = p.id
  );
