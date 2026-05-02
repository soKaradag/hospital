create table if not exists operating_rooms (
    id varchar(36) not null,
    department_id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    active bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_operating_rooms primary key (id),
    constraint fk_operating_rooms_department foreign key (department_id) references departments(id),
    constraint uk_operating_rooms_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists surgery_requests (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    requested_by_doctor_id varchar(36) not null,
    procedure_code varchar(100) not null,
    procedure_name varchar(150) not null,
    priority varchar(40) not null,
    status varchar(40) not null,
    preferred_date date,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_surgery_requests primary key (id),
    constraint fk_surgery_requests_encounter foreign key (encounter_id) references encounters(id),
    constraint fk_surgery_requests_doctor foreign key (requested_by_doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists surgeries (
    id varchar(36) not null,
    surgery_request_id varchar(36) not null,
    patient_id varchar(36) not null,
    primary_doctor_id varchar(36) not null,
    operating_room_id varchar(36) not null,
    scheduled_at datetime(6) not null,
    status varchar(40) not null,
    inventory_status varchar(40) not null,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_surgeries primary key (id),
    constraint fk_surgeries_request foreign key (surgery_request_id) references surgery_requests(id),
    constraint fk_surgeries_patient foreign key (patient_id) references patients(id),
    constraint fk_surgeries_doctor foreign key (primary_doctor_id) references doctors(id),
    constraint fk_surgeries_operating_room foreign key (operating_room_id) references operating_rooms(id),
    constraint uk_surgeries_request unique (surgery_request_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists surgery_team_assignments (
    id varchar(36) not null,
    surgery_id varchar(36) not null,
    doctor_id varchar(36) not null,
    role_name varchar(80) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_surgery_team_assignments primary key (id),
    constraint fk_surgery_team_assignments_surgery foreign key (surgery_id) references surgeries(id),
    constraint fk_surgery_team_assignments_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists surgery_status_history (
    id varchar(36) not null,
    surgery_id varchar(36) not null,
    status varchar(40) not null,
    changed_at datetime(6) not null,
    note varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_surgery_status_history primary key (id),
    constraint fk_surgery_status_history_surgery foreign key (surgery_id) references surgeries(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;
