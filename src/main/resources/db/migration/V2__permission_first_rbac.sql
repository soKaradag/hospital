create table if not exists roles (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(100) not null,
    description varchar(255),
    is_system bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_roles primary key (id),
    constraint uk_roles_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists permissions (
    id varchar(36) not null,
    code varchar(100) not null,
    name varchar(150) not null,
    description varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_permissions primary key (id),
    constraint uk_permissions_code unique (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table if not exists role_permissions (
    id varchar(36) not null,
    role_id varchar(36) not null,
    permission_id varchar(36) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_role_permissions primary key (id),
    constraint fk_role_permissions_role foreign key (role_id) references roles(id),
    constraint fk_role_permissions_permission foreign key (permission_id) references permissions(id),
    constraint uk_role_permissions_role_permission unique (role_id, permission_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_role_permissions_role_id on role_permissions (role_id);
create index idx_role_permissions_permission_id on role_permissions (permission_id);

create table if not exists user_roles (
    id varchar(36) not null,
    user_id varchar(36) not null,
    role_id varchar(36) not null,
    is_primary bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint pk_user_roles primary key (id),
    constraint fk_user_roles_user foreign key (user_id) references users(id),
    constraint fk_user_roles_role foreign key (role_id) references roles(id),
    constraint uk_user_roles_user_role unique (user_id, role_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create index idx_user_roles_user_id on user_roles (user_id);
create index idx_user_roles_role_id on user_roles (role_id);

insert into roles (id, code, name, description, is_system, created_at, updated_at) values
    ('f0f9eb27-43c6-4d7c-82df-5f0d52e93f10', 'ADMIN', 'Admin', 'System administrator role', b'1', current_timestamp(6), current_timestamp(6)),
    ('a1e747a9-b9b8-47d9-aeb8-8b6af3fdf211', 'DOCTOR', 'Doctor', 'Doctor role', b'1', current_timestamp(6), current_timestamp(6)),
    ('a82ae484-e499-40f1-a925-75e2cef4b312', 'RECEPTIONIST', 'Receptionist', 'Reception operations role', b'1', current_timestamp(6), current_timestamp(6)),
    ('a18e2dfd-6a1a-49b8-b53f-b9d2aeed7d13', 'CASHIER', 'Cashier', 'Cashier role', b'1', current_timestamp(6), current_timestamp(6)),
    ('d58d5447-1ac4-4d4f-8b74-bf1f17169f14', 'NURSE', 'Nurse', 'Nurse role', b'1', current_timestamp(6), current_timestamp(6));

insert into permissions (id, code, name, description, created_at, updated_at) values
    ('0eb08a6d-629b-40fb-a81d-fdf001c6a101', 'auth.read', 'Read auth session', 'Allows reading current authenticated user details', current_timestamp(6), current_timestamp(6)),
    ('512833d3-2d3e-4f3a-9f08-d534498d9102', 'auth.write', 'Manage auth session', 'Allows writing authenticated session state such as logout', current_timestamp(6), current_timestamp(6)),
    ('8497fdbf-2640-4d0b-a3a2-99a5e753d103', 'departments.read', 'Read departments', 'Allows reading departments', current_timestamp(6), current_timestamp(6)),
    ('5a0647ec-8f65-4370-98f6-89cbc2ed5f04', 'departments.write', 'Write departments', 'Allows creating and updating departments', current_timestamp(6), current_timestamp(6)),
    ('495db60f-f6ab-4d56-b31b-6e31cf6fe205', 'doctors.read', 'Read doctors', 'Allows reading doctors', current_timestamp(6), current_timestamp(6)),
    ('14b355cc-9721-4465-838c-843faeb17306', 'doctors.write', 'Write doctors', 'Allows creating and updating doctors', current_timestamp(6), current_timestamp(6)),
    ('9ab0bb41-ef94-430d-a0a7-04a1fdca0c07', 'patients.read', 'Read patients', 'Allows reading patients', current_timestamp(6), current_timestamp(6)),
    ('317932d2-f4d0-4e39-b797-63a179b17508', 'patients.write', 'Write patients', 'Allows creating and updating patients', current_timestamp(6), current_timestamp(6)),
    ('31b6ffb5-3280-40f9-b786-6ce63ff0b209', 'appointments.read', 'Read appointments', 'Allows reading appointments', current_timestamp(6), current_timestamp(6)),
    ('f3d7a5f2-7c3d-4b39-b1c1-ad1ca8eb8d10', 'appointments.write', 'Write appointments', 'Allows creating and updating appointments', current_timestamp(6), current_timestamp(6)),
    ('2a6bfa2a-8826-4036-803e-a653548ee611', 'encounters.read', 'Read encounters', 'Allows reading encounters', current_timestamp(6), current_timestamp(6)),
    ('b04808cc-9e4d-4f0c-b2e6-a0c1e26dfc12', 'encounters.write', 'Write encounters', 'Allows creating and updating encounters', current_timestamp(6), current_timestamp(6)),
    ('d501c436-68ca-4307-98d1-2d3c6329a113', 'payments.read', 'Read payments', 'Allows reading payments', current_timestamp(6), current_timestamp(6)),
    ('422c2b17-4d0f-42d8-a634-bddf4dd0bd14', 'payments.write', 'Write payments', 'Allows creating and updating payments', current_timestamp(6), current_timestamp(6)),
    ('3e2ab27c-0ef2-412f-8b68-433c44cf3615', 'doctor-schedules.read', 'Read doctor schedules', 'Allows reading doctor schedules', current_timestamp(6), current_timestamp(6)),
    ('7b6c9ad3-34b6-4cdd-bd5c-b996cb429e16', 'doctor-schedules.write', 'Write doctor schedules', 'Allows creating and updating doctor schedules', current_timestamp(6), current_timestamp(6)),
    ('272e5ff0-8c96-4ca3-9859-f78bbf0dc717', 'diseases.read', 'Read diseases', 'Allows reading disease catalog entries', current_timestamp(6), current_timestamp(6)),
    ('94f0a772-f026-4b84-aabd-152e7d180118', 'diseases.write', 'Write diseases', 'Allows creating and updating disease catalog entries', current_timestamp(6), current_timestamp(6)),
    ('01b06eb1-9d3a-427c-9b83-286276f4c219', 'patient-diseases.read', 'Read patient diseases', 'Allows reading patient disease records', current_timestamp(6), current_timestamp(6)),
    ('a9a430d6-1a72-4a7e-a6f8-18454d2d7e20', 'patient-diseases.write', 'Write patient diseases', 'Allows creating and updating patient disease records', current_timestamp(6), current_timestamp(6)),
    ('c6880baf-4ff4-4cbe-b2aa-b91753fe9421', 'encounter-diagnoses.read', 'Read encounter diagnoses', 'Allows reading encounter diagnosis records', current_timestamp(6), current_timestamp(6)),
    ('6abcb98f-75f8-4017-9bf7-2c1bc3979622', 'encounter-diagnoses.write', 'Write encounter diagnoses', 'Allows creating and updating encounter diagnosis records', current_timestamp(6), current_timestamp(6)),
    ('fd1c1a55-b92e-4678-838a-97b97a499b23', 'prescriptions.read', 'Read prescriptions', 'Allows reading prescriptions', current_timestamp(6), current_timestamp(6)),
    ('cf84ff38-b724-448e-93d9-90467c882224', 'prescriptions.write', 'Write prescriptions', 'Allows creating and updating prescriptions', current_timestamp(6), current_timestamp(6)),
    ('19d51183-98b8-4d71-a124-a393a9734925', 'reports.read', 'Read reports', 'Allows reading reporting endpoints', current_timestamp(6), current_timestamp(6));

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select uuid(), r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'ADMIN';

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '3f8b758d-f1d3-4bd0-9782-8e30464e5502', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'auth.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'fa78d5a6-f5d3-4f8e-8e51-11ba411ca803', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'auth.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '5ac16c25-e657-4b2b-bb8f-7221f7a6fd04', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'departments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'b7f4d0f7-218b-4d56-9272-8bc9fcb0c405', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'departments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '54466860-2f5f-4ba0-a278-b3e44bcb6606', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'doctors.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '8c1c0c9a-c2cd-4e8e-801d-c27ac97c0e07', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'doctors.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'b86df619-96f7-4d43-a2dd-f839d49b2708', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'patients.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2692e225-fde0-4551-8e95-7e489c279909', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'patients.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '8695af08-7218-4a86-886a-cde57b893b10', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'appointments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2cb35f0b-3e4f-41f7-8670-f5d173ebcb11', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'appointments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'a6f48395-e661-44c5-804a-43e748e64e12', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'encounters.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '0fb80c85-f5a5-4e9f-90fd-9ae0bdb1ba13', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'encounters.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'c0f5aa8a-287f-490e-8a41-c3d74067fe14', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'payments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'f04cb054-b938-4da6-a5f8-7db95e804e15', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'payments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'fd378d72-e066-4b59-94c8-0f162add8b16', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'doctor-schedules.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '869eaf64-98d7-4321-bda6-c2862c4ebd17', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'diseases.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'd351ca1a-17c6-4e4f-85e7-ad29e034b718', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'diseases.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'dd719ca5-c4fb-45e8-bbb3-f4853714a719', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'patient-diseases.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'bd5701d2-a6ef-41de-8a20-3cdd9cb04520', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'patient-diseases.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '78aedb2d-25bd-431e-b865-5fb8ff20bd21', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'encounter-diagnoses.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2b7a1325-79d9-4d61-8ec5-1bc95d0a8722', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'encounter-diagnoses.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '39474743-eb0d-4104-8297-f4f1e2db1223', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'prescriptions.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '49dc055b-1fc0-4ebc-b293-136152ae5e24', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'prescriptions.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'ae8359fb-fd7f-454c-9d65-2e4662403e25', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'DOCTOR' and p.code = 'reports.read';

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'a7618701-ca6d-4401-b098-316274653b01', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'auth.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '1963e86a-914e-4df2-8d3d-9a0f6f5dad02', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'auth.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '3bebd4af-f490-4675-a021-dd3aa91f8a03', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'departments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '6fb7a9ec-e4ea-44a2-b1dd-0d15b86bb104', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'departments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2e2335d6-78c9-4d25-a953-3f90b45a3c05', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'doctors.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '63d8f5e6-9f1e-485d-a36c-e5fa3b895306', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'doctors.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '65f32c52-f669-4db0-a559-6b8d0cae5a07', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'patients.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'a4573a85-b813-43ad-bb51-f1d445330308', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'patients.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '183689db-8071-4dae-b88c-36800d8ca609', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'appointments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'e5e2b91e-bf07-4310-8e4a-8fb38ed0ec10', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'appointments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'eef481fe-a527-419c-9f0c-844a22b10211', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'encounters.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'da5f486e-a6c4-4725-bb2f-f7eb96b3b212', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'encounters.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '74859b1a-cc95-4985-90ec-53ac659c3813', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'payments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2cf90c93-f184-4a9f-b6f2-e4045f91ad14', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'payments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '63bf5fb0-9b0a-4d5a-9b53-d8cbe2644015', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'doctor-schedules.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '289c5044-fc2a-4e29-a867-1f820e748616', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'doctor-schedules.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '5a4c7270-f210-46e3-b0e3-7f6c765bb417', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'diseases.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'ba3eeece-c284-45f2-96cf-4b06d94ac918', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'patient-diseases.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '3a55358d-c676-4462-b2ae-f2dcf9945019', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'encounter-diagnoses.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '1ef2d48a-6042-4bdf-b725-7a7f537b3020', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'prescriptions.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '15bf881a-f03b-4f0e-b3f3-b4c516636621', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'RECEPTIONIST' and p.code = 'reports.read';

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '688d6aec-eead-4ab6-ab0f-15e122589701', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'auth.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '905a6176-6c1a-4454-93cf-2cb7fa191702', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'auth.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '70077018-3bfd-4429-9596-40425b59d303', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'departments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '1174a81d-d2f5-48bc-8eaa-9ab72c52f304', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'departments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2c7e4da0-11d4-49d4-aa86-d1042b9c3105', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'doctors.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '1a6fbe13-aa2b-4eeb-8e08-b0d5da3b2f06', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'doctors.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '67144e0a-f5dd-4eea-a6e6-8cf7cb76f107', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'patients.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '42063f11-4fda-4e16-8a5b-a28da4d8f508', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'patients.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '809324b6-64ab-489c-a7ea-7f390fd13009', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'appointments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '9f0e5d77-d4a6-47c2-848d-f7e766225810', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'appointments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'd6d16f8f-aadf-4cbd-9861-926e5d4edc11', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'encounters.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '96829c04-5ca7-4c8e-97d4-f1f70866dc12', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'encounters.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'f1c84204-fb19-4c16-a538-ee1e64128f13', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'payments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'de03c15e-b635-4448-87bc-d179c3395a14', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'payments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '49968172-b919-42c8-a029-b44bc2d1c115', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'CASHIER' and p.code = 'reports.read';

insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '42f08836-a08e-4c4d-ae15-71328cd16e01', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'auth.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '0f7abed8-1ed6-438d-a424-47d81f233902', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'auth.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '04b3bb2c-e432-4fd3-b457-3b87dd4c6a03', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'departments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '393ce6a5-bdc4-46f2-831b-b1b148f6d404', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'departments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '798f493a-ceb0-43e5-a40c-6ffb13e82605', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'doctors.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2c910b62-a436-4a92-b42f-daf5876aeb06', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'doctors.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'b966ab3f-f1d6-4330-b611-e0ec69cc2907', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'patients.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'bd2fed0b-e6ef-4370-af52-16d7e1da6208', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'patients.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '5dc3de0c-7000-40c8-b238-af2fe8a0ee09', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'appointments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'ebea486d-cc79-4c0a-9c3a-b1c7d3c2b310', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'appointments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '27ccdba0-b4a0-4d78-83c3-c0c62fdbb211', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'encounters.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '4d78dc1d-2ac0-40cf-a7a3-6bf8a2db3012', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'encounters.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2f68117b-c934-4d86-8840-e4aad1f74013', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'payments.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '7e5a52de-d04d-408e-bf9d-f85d03fd8a14', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'payments.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'fd43dc20-b924-4764-a439-2bf2653f9815', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'doctor-schedules.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '67643269-2ab2-4910-806e-bec3f44a1116', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'diseases.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '75762b46-99c8-45ca-b0bd-d9a4df803217', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'patient-diseases.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '2d06f7b0-b3e5-4136-a865-ef308204c918', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'patient-diseases.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'a0ceb761-0c17-4946-9529-8c68dcb2fc19', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'encounter-diagnoses.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'ab5b2044-c437-4d92-abff-2f31056d7b20', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'encounter-diagnoses.write';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select 'f21367b3-1cc7-4e6a-90f8-3f08a6407621', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'prescriptions.read';
insert into role_permissions (id, role_id, permission_id, created_at, updated_at)
select '4af2f9ce-2f24-4ee1-a8a3-2534373aab22', r.id, p.id, current_timestamp(6), current_timestamp(6)
from roles r join permissions p on r.code = 'NURSE' and p.code = 'reports.read';
