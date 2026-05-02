create table report_snapshots (
    id varchar(36) not null,
    report_code varchar(100) not null,
    generated_at datetime(6) not null,
    row_count int not null,
    snapshot_summary varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id)
);

create table report_export_jobs (
    id varchar(36) not null,
    report_snapshot_id varchar(36) not null,
    export_format varchar(20) not null,
    status varchar(30) not null,
    requested_at datetime(6) not null,
    completed_at datetime(6),
    output_location varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_report_export_jobs_snapshot foreign key (report_snapshot_id) references report_snapshots (id)
);

create index idx_report_snapshots_report_code on report_snapshots (report_code);
create index idx_report_export_jobs_snapshot_id on report_export_jobs (report_snapshot_id);
