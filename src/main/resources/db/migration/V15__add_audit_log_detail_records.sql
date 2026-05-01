create table audit_log_details (
    id varchar(36) not null,
    audit_log_id varchar(36) not null,
    detail_key varchar(100) not null,
    detail_value varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_audit_log_details_audit_log foreign key (audit_log_id) references audit_logs (id)
);

insert into audit_log_details (id, audit_log_id, detail_key, detail_value, created_at, updated_at)
select uuid(), al.id, 'message', al.message, current_timestamp(6), current_timestamp(6)
from audit_logs al
where al.message is not null and trim(al.message) <> '';

create index idx_audit_log_details_audit_log_id on audit_log_details (audit_log_id);
