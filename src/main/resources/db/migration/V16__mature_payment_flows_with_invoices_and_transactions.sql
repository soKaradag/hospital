create table invoices (
    id varchar(36) not null,
    payment_id varchar(36) not null,
    invoice_number varchar(100) not null,
    issued_at datetime(6) not null,
    total_amount decimal(12, 2) not null,
    currency varchar(3) not null,
    status varchar(30) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_invoices_payment foreign key (payment_id) references payments (id),
    constraint uk_invoices_invoice_number unique (invoice_number)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table payment_transactions (
    id varchar(36) not null,
    payment_id varchar(36) not null,
    transaction_reference varchar(100) not null,
    processed_at datetime(6) not null,
    amount decimal(12, 2) not null,
    status varchar(30) not null,
    channel varchar(30) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_payment_transactions_payment foreign key (payment_id) references payments (id),
    constraint uk_payment_transactions_reference unique (transaction_reference)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

create table payment_refunds (
    id varchar(36) not null,
    payment_id varchar(36) not null,
    refunded_at datetime(6) not null,
    amount decimal(12, 2) not null,
    reason varchar(255),
    status varchar(30) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_payment_refunds_payment foreign key (payment_id) references payments (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into invoices (id, payment_id, invoice_number, issued_at, total_amount, currency, status, created_at, updated_at)
select
    uuid(),
    p.id,
    concat('INV-', replace(p.id, '-', '')),
    coalesce(p.paid_at, p.created_at),
    p.amount,
    p.currency,
    p.payment_status,
    current_timestamp(6),
    current_timestamp(6)
from payments p;

insert into payment_transactions (id, payment_id, transaction_reference, processed_at, amount, status, channel, created_at, updated_at)
select
    uuid(),
    p.id,
    concat('TXN-', replace(p.id, '-', '')),
    coalesce(p.paid_at, p.created_at),
    p.amount,
    p.payment_status,
    p.payment_method,
    current_timestamp(6),
    current_timestamp(6)
from payments p;

insert into payment_refunds (id, payment_id, refunded_at, amount, reason, status, created_at, updated_at)
select
    uuid(),
    p.id,
    current_timestamp(6),
    p.amount,
    'Auto-created refund placeholder for cancelled payment',
    'REQUESTED',
    current_timestamp(6),
    current_timestamp(6)
from payments p
where p.payment_status = 'CANCELLED';

create index idx_invoices_payment_id on invoices (payment_id);
create index idx_payment_transactions_payment_id on payment_transactions (payment_id);
create index idx_payment_refunds_payment_id on payment_refunds (payment_id);
