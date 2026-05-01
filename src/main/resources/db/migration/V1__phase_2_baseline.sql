create table if not exists departments (
    id varchar(36) not null,
    name varchar(100) not null,
    description varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_departments primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists doctors (
    id varchar(36) not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    specialization varchar(100),
    phone_country_code varchar(10),
    phone_number varchar(20),
    email varchar(150),
    department_id varchar(36) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_doctors primary key (id),
    constraint fk_doctors_department foreign key (department_id) references departments(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_doctors_department_id on doctors (department_id);

create table if not exists patients (
    id varchar(36) not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    national_id varchar(20),
    birth_date date,
    gender varchar(20),
    phone_country_code varchar(10),
    phone_number varchar(20),
    email varchar(150),
    country_code varchar(10),
    country_name varchar(100),
    city_code varchar(20),
    city_name varchar(100),
    district varchar(100),
    postal_code varchar(20),
    address_line varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_patients primary key (id),
    constraint uk_patients_national_id unique (national_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists appointments (
    id varchar(36) not null,
    patient_id varchar(36) not null,
    doctor_id varchar(36) not null,
    appointment_date_time datetime(6) not null,
    status varchar(30) not null,
    notes varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_appointments primary key (id),
    constraint fk_appointments_patient foreign key (patient_id) references patients(id),
    constraint fk_appointments_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_appointments_patient_id on appointments (patient_id);
create index idx_appointments_doctor_id on appointments (doctor_id);
create index idx_appointments_date_time on appointments (appointment_date_time);

create table if not exists encounters (
    id varchar(36) not null,
    appointment_id varchar(36),
    patient_id varchar(36) not null,
    doctor_id varchar(36) not null,
    complaint varchar(500),
    diagnosis_note varchar(1000),
    treatment_note varchar(1000),
    encounter_date_time datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_encounters primary key (id),
    constraint fk_encounters_appointment foreign key (appointment_id) references appointments(id),
    constraint fk_encounters_patient foreign key (patient_id) references patients(id),
    constraint fk_encounters_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_encounters_appointment_id on encounters (appointment_id);
create index idx_encounters_patient_id on encounters (patient_id);
create index idx_encounters_doctor_id on encounters (doctor_id);
create index idx_encounters_date_time on encounters (encounter_date_time);

create table if not exists payments (
    id varchar(36) not null,
    patient_id varchar(36) not null,
    encounter_id varchar(36) not null,
    amount decimal(12,2) not null,
    currency varchar(3) not null,
    payment_method varchar(20) not null,
    payment_status varchar(20) not null,
    paid_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_payments primary key (id),
    constraint fk_payments_patient foreign key (patient_id) references patients(id),
    constraint fk_payments_encounter foreign key (encounter_id) references encounters(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_payments_patient_id on payments (patient_id);
create index idx_payments_encounter_id on payments (encounter_id);
create index idx_payments_paid_at on payments (paid_at);

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

create table if not exists diseases (
    id varchar(36) not null,
    code varchar(50) not null,
    name varchar(150) not null,
    description varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_diseases primary key (id),
    constraint uk_diseases_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists patient_diseases (
    id varchar(36) not null,
    patient_id varchar(36) not null,
    disease_id varchar(36) not null,
    diagnosed_at datetime(6),
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_patient_diseases primary key (id),
    constraint fk_patient_diseases_patient foreign key (patient_id) references patients(id),
    constraint fk_patient_diseases_disease foreign key (disease_id) references diseases(id),
    constraint uk_patient_diseases_patient_disease unique (patient_id, disease_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_patient_diseases_patient_id on patient_diseases (patient_id);
create index idx_patient_diseases_disease_id on patient_diseases (disease_id);

create table if not exists encounter_diagnoses (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    disease_id varchar(36) not null,
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_encounter_diagnoses primary key (id),
    constraint fk_encounter_diagnoses_encounter foreign key (encounter_id) references encounters(id),
    constraint fk_encounter_diagnoses_disease foreign key (disease_id) references diseases(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_encounter_diagnoses_encounter_id on encounter_diagnoses (encounter_id);
create index idx_encounter_diagnoses_disease_id on encounter_diagnoses (disease_id);

create table if not exists prescriptions (
    id varchar(36) not null,
    encounter_id varchar(36) not null,
    patient_id varchar(36) not null,
    doctor_id varchar(36) not null,
    prescription_date date not null,
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_prescriptions primary key (id),
    constraint fk_prescriptions_encounter foreign key (encounter_id) references encounters(id),
    constraint fk_prescriptions_patient foreign key (patient_id) references patients(id),
    constraint fk_prescriptions_doctor foreign key (doctor_id) references doctors(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_prescriptions_encounter_id on prescriptions (encounter_id);
create index idx_prescriptions_patient_id on prescriptions (patient_id);
create index idx_prescriptions_doctor_id on prescriptions (doctor_id);
create index idx_prescriptions_prescription_date on prescriptions (prescription_date);

create table if not exists payment_audit (
    id varchar(36) not null,
    payment_id varchar(36) not null,
    patient_id varchar(36) not null,
    encounter_id varchar(36) not null,
    action varchar(20) not null,
    amount decimal(12,2) not null,
    currency varchar(3) not null,
    payment_method varchar(20) not null,
    payment_status varchar(20) not null,
    paid_at datetime(6),
    logged_at datetime(6) not null,
    constraint pk_payment_audit primary key (id),
    constraint fk_payment_audit_payment foreign key (payment_id) references payments(id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_payment_audit_payment_id on payment_audit (payment_id);
create index idx_payment_audit_patient_id on payment_audit (patient_id);
create index idx_payment_audit_logged_at on payment_audit (logged_at);
