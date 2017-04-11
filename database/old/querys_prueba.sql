select * from job_execution_vw
drop table job_execution_vw_mater;
create table job_execution_vw_mater as select * from job_execution_vw;

truncate table job_execution_vw_mater;

insert into job_execution_vw_mater select * from job_execution_vw;


select * from job_execution_vw_mater;



select activity_type, min(standard_value), max(standard_value),  max(standard_value), count(*)  
from job_data_filtered_vw 
where job_execution_id =50
group by activity_type
order by count(*) DESC; 

select job_data_raw_id,activity_type, standard_value,standard_units, log(cast (standard_value as float(3))) pic50, 
width_bucket(log(cast (standard_value as float(3))), 0, 10, 20) as bucket, 
CAST(((width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)-1)*0.5) as varchar) || ' to ' || CAST( (width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)*0.5) as varchar)


from job_data_filtered_vw 
where job_execution_id =50 and activity_type in ('IC50') 
order by width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)



select count(*), width_bucket(log(cast (standard_value as float(3))), 0, 10, 20),
CAST(((width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)-1)*0.5) as varchar) || ' to ' || CAST( (width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)*0.5) as varchar)

from job_data_filtered_vw 
where job_execution_id =50 and activity_type in ('IC50') 
group by width_bucket(log(cast (standard_value as float(3))), 0, 10, 20) ,CAST(((width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)-1)*0.5) as varchar) || ' to ' || CAST( (width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)*0.5) as varchar)
order by width_bucket(log(cast (standard_value as float(3))), 0, 10, 20)



select job_data_raw_id,activity_type, standard_value,standard_units
from job_data_filtered_vw 
where job_execution_id =50 and cast (standard_value as float) <0

