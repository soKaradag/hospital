create table appointment_status_history (
    id varchar(36) not null,
    appointment_id varchar(36) not null,
    status varchar(30) not null,
    notes varchar(500),
    changed_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_appointment_status_history_appointment foreign key (appointment_id) references appointments (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into appointment_status_history (id, appointment_id, status, notes, changed_at, created_at, updated_at)
select
    uuid(),
    a.id,
    a.status,
    a.notes,
    a.updated_at,
    current_timestamp(6),
    current_timestamp(6)
from appointments a;

create index idx_appointment_status_history_appointment_id on appointment_status_history (appointment_id);
