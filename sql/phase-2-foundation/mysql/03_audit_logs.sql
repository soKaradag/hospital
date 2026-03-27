-- audit_logs tablosu başarılı ve başarısız iş akışlarının kalıcı kayıtlarını tutar.
create table if not exists audit_logs (
    id varchar(36) not null,
    action varchar(100) not null,
    entity_name varchar(100) not null,
    description varchar(255),
    status varchar(40) not null,
    message varchar(500),
    error_code varchar(50),
    actor_user_id varchar(36),
    actor_role varchar(30),
    request_path varchar(255),
    http_method varchar(10),
    occurred_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_audit_logs primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_audit_logs_actor_user_id on audit_logs (actor_user_id);
create index idx_audit_logs_action on audit_logs (action);
create index idx_audit_logs_status on audit_logs (status);
create index idx_audit_logs_occurred_at on audit_logs (occurred_at);
