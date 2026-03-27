-- users tablosu sisteme giriş yapabilen kullanıcıları tutar.
create table if not exists users (
    id varchar(36) not null,
    username varchar(100) not null,
    password_hash varchar(255) not null,
    role varchar(30) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_users primary key (id),
    constraint uk_users_username unique (username)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- user_info tablosu auth dışı kullanıcı profil bilgilerini tutar.
create table if not exists user_info (
    id varchar(36) not null,
    user_id varchar(36) not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    phone_country_code varchar(10),
    phone_number varchar(20),
    email varchar(150),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_user_info primary key (id),
    constraint uk_user_info_user_id unique (user_id),
    constraint fk_user_info_user foreign key (user_id) references users(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_user_info_user_id on user_info (user_id);
