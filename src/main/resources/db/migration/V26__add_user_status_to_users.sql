alter table users
    add column status varchar(30) not null default 'ACTIVE' after password_hash;

update users
set status = 'ACTIVE'
where status is null;
