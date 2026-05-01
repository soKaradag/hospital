create table appointment_reminders (
    id varchar(36) not null,
    appointment_id varchar(36) not null,
    reminder_type varchar(50) not null,
    scheduled_at datetime(6) not null,
    sent_at datetime(6),
    status varchar(30) not null,
    channel varchar(30) not null,
    message varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_appointment_reminders_appointment foreign key (appointment_id) references appointments (id)
);

insert into appointment_reminders (id, appointment_id, reminder_type, scheduled_at, sent_at, status, channel, message, created_at, updated_at)
select
    uuid(),
    a.id,
    'PRE_VISIT',
    timestampadd(hour, -24, a.appointment_date_time),
    null,
    'SCHEDULED',
    'SMS',
    concat('Reminder for appointment ', a.id),
    current_timestamp(6),
    current_timestamp(6)
from appointments a
where a.appointment_date_time > timestampadd(hour, 24, current_timestamp(6));

create index idx_appointment_reminders_appointment_id on appointment_reminders (appointment_id);
