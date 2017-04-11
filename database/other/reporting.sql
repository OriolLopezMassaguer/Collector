drop view job_data_raw_reporting

CREATE OR REPLACE VIEW job_data_raw_reporting AS 

SELECT 
	--j.job_description,
	j.uniprotid,
	jext.job_extraction_description,
	to_char(jexec.job_execution_finish_filtering_date,'YYYYMMDD') extraction_date,
	jesummary.compounds_raw,
	jesummary.activities_raw,
	draw.*
FROM 
	job_data_raw draw, 
	job_execution jexec, 
	job j,
	job_extraction jext,
	job_execution_vw_mater jesummary
where 
	draw.job_execution_id=jexec.job_execution_id
	and j.job_id=jexec.job_id
	and j.job_extraction_id=jext.job_extraction_id
	and jexec.job_execution_id=jesummary.job_execution_id
	and draw.activity_type in ('AC50','IC50','Potency','Inhibition','Ki','Activity','EC50')

	
	limit 100

select count(*) from job_data_raw_reporting

create table job_data_raw_reporting_mater as select * from job_data_raw_reporting