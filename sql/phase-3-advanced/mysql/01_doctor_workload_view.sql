-- doctor_workload_view, doktor bazlı operasyonel iş yükünü tek sorguda raporlamak için hazırlanır.
-- View kullanımı sayesinde uygulama tarafı karmaşık join ve aggregation detaylarını tekrar yazmak zorunda kalmaz.
drop view if exists doctor_workload_view;

create view doctor_workload_view as
select
    d.id as doctor_id,
    concat(d.first_name, ' ', d.last_name) as doctor_full_name,
    dep.name as department_name,
    count(distinct a.id) as appointment_count,
    count(distinct e.id) as encounter_count,
    count(distinct pr.id) as prescription_count
from doctors d
join departments dep on dep.id = d.department_id
left join appointments a on a.doctor_id = d.id
left join encounters e on e.doctor_id = d.id
left join prescriptions pr on pr.doctor_id = d.id
group by
    d.id,
    d.first_name,
    d.last_name,
    dep.name;
