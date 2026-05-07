alter table doctors
    add column user_id varchar(36) null after department_id;

alter table doctors
    add constraint uk_doctors_user_id unique (user_id);

alter table doctors
    add constraint fk_doctors_user foreign key (user_id) references users(id);

create index idx_doctors_user_id on doctors (user_id);
