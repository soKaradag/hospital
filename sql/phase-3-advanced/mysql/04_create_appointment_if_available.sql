-- Procedure tanımını tekrar çalıştırılabilir tutmak için önce eski sürüm kaldırılır.
drop procedure if exists create_appointment_if_available;

delimiter $$

-- Bu procedure aynı doktor için aynı tarih-saatte ikinci bir randevu oluşmasını veritabanı seviyesinde engeller.
-- Uygulama katmanı UUID'yi üretip procedure'e gönderir; böylece mevcut BaseEntity yaklaşımı korunur.
create procedure create_appointment_if_available(
    in p_appointment_id varchar(36),
    in p_patient_id varchar(36),
    in p_doctor_id varchar(36),
    in p_appointment_date_time datetime(6),
    in p_status varchar(30),
    in p_notes varchar(500),
    out p_conflict_found boolean
)
begin
    declare v_conflict_count int default 0;

    select count(*)
      into v_conflict_count
      from appointments a
     where a.doctor_id = p_doctor_id
       and a.appointment_date_time = p_appointment_date_time;

    if v_conflict_count > 0 then
        set p_conflict_found = true;
    else
        insert into appointments (
            id,
            patient_id,
            doctor_id,
            appointment_date_time,
            status,
            notes,
            created_at,
            updated_at
        ) values (
            p_appointment_id,
            p_patient_id,
            p_doctor_id,
            p_appointment_date_time,
            p_status,
            p_notes,
            utc_timestamp(6),
            utc_timestamp(6)
        );

        set p_conflict_found = false;
    end if;
end$$

delimiter ;
