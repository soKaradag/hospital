-- doctor_schedules tablosu doktorların haftalık çalışma planını tutar.
create table if not exists doctor_schedules (
    id varchar(36) not null,
    doctor_id varchar(36) not null,
    day_of_week varchar(20) not null,
    start_time time not null,
    end_time time not null,
    is_active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_doctor_schedules primary key (id),
    constraint fk_doctor_schedules_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_doctor_schedules_doctor_id on doctor_schedules (doctor_id);
create index idx_doctor_schedules_day_of_week on doctor_schedules (day_of_week);
