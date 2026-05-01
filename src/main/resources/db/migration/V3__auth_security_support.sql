create table if not exists login_attempts (
    id varchar(36) not null,
    user_id varchar(36),
    username varchar(100) not null,
    success bit not null,
    failure_reason varchar(255),
    ip_address varchar(45),
    user_agent varchar(255),
    attempted_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_login_attempts primary key (id),
    constraint fk_login_attempts_user foreign key (user_id) references users(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_login_attempts_user_id on login_attempts (user_id);
create index idx_login_attempts_username on login_attempts (username);
create index idx_login_attempts_attempted_at on login_attempts (attempted_at);

create table if not exists password_reset_tokens (
    id varchar(36) not null,
    user_id varchar(36) not null,
    token_hash varchar(255) not null,
    expires_at datetime(6) not null,
    used_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_password_reset_tokens primary key (id),
    constraint uk_password_reset_tokens_token_hash unique (token_hash),
    constraint fk_password_reset_tokens_user foreign key (user_id) references users(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_password_reset_tokens_user_id on password_reset_tokens (user_id);
create index idx_password_reset_tokens_expires_at on password_reset_tokens (expires_at);
