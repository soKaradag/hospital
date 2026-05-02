create table doctor_leaves (
    id varchar(36) not null,
    doctor_id varchar(36) not null,
    start_date date not null,
    end_date date not null,
    reason varchar(255),
    leave_type varchar(100),
    approved bit not null default b'1',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_doctor_leaves_doctor foreign key (doctor_id) references doctors (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table doctor_schedule_exceptions (
    id varchar(36) not null,
    doctor_schedule_id varchar(36) not null,
    exception_date date not null,
    override_start_time time,
    override_end_time time,
    available bit not null default b'1',
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_doctor_schedule_exceptions_schedule foreign key (doctor_schedule_id) references doctor_schedules (id),
    constraint uk_doctor_schedule_exceptions_schedule_date unique (doctor_schedule_id, exception_date)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_doctor_leaves_doctor_id on doctor_leaves (doctor_id);
create index idx_doctor_schedule_exceptions_schedule_id on doctor_schedule_exceptions (doctor_schedule_id);
