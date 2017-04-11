ALTER SEQUENCE job_execution_job_execution_id_seq RESTART WITH 139;

select max(job_execution_id) from job_execution


ALTER SEQUENCE job_data_raw_job_data_raw_id_seq RESTART WITH 700000;

select max(job_data_raw_id) from job_data_raw