create table rooms (
    id varchar(36) not null,
    department_id varchar(36) not null,
    room_number varchar(50) not null,
    room_type varchar(100),
    floor_number int,
    active bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_rooms_department foreign key (department_id) references departments (id),
    constraint uk_rooms_department_room_number unique (department_id, room_number)
);

create table department_service_catalog (
    id varchar(36) not null,
    department_id varchar(36) not null,
    service_code varchar(100) not null,
    service_name varchar(150) not null,
    description varchar(255),
    active bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_department_service_catalog_department foreign key (department_id) references departments (id),
    constraint uk_department_service_catalog_department_service_code unique (department_id, service_code)
);

create index idx_rooms_department_id on rooms (department_id);
create index idx_department_service_catalog_department_id on department_service_catalog (department_id);
