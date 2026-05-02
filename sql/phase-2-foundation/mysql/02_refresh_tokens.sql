-- refresh_tokens tablosu kullanıcıların yenileme token kayıtlarını tutar.
create table if not exists refresh_tokens (
    id varchar(36) not null,
    user_id varchar(36) not null,
    token_hash varchar(255) not null,
    expires_at datetime(6) not null,
    revoked_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_refresh_tokens primary key (id),
    constraint uk_refresh_tokens_token_hash unique (token_hash),
    constraint fk_refresh_tokens_user foreign key (user_id) references users(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_refresh_tokens_user_id on refresh_tokens (user_id);
create index idx_refresh_tokens_expires_at on refresh_tokens (expires_at);
