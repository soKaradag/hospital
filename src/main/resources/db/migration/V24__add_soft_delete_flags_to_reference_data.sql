alter table departments
    add column active bit not null default b'1';

alter table doctors
    add column active bit not null default b'1';

alter table diseases
    add column active bit not null default b'1';
