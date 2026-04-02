-- Trigger tanımını tekrar çalıştırılabilir tutmak için önce eski trigger silinir.
drop trigger if exists trg_payments_after_insert;

delimiter $$

-- AFTER INSERT trigger'ı, payment kaydı başarılı şekilde oluştuğunda audit tablosuna iz düşer.
create trigger trg_payments_after_insert
after insert on payments
for each row
begin
    insert into payment_audit (
        id,
        payment_id,
        patient_id,
        encounter_id,
        action,
        amount,
        currency,
        payment_method,
        payment_status,
        paid_at,
        logged_at
    ) values (
        uuid(),
        new.id,
        new.patient_id,
        new.encounter_id,
        'INSERT',
        new.amount,
        new.currency,
        new.payment_method,
        new.payment_status,
        new.paid_at,
        utc_timestamp(6)
    );
end$$

delimiter ;
