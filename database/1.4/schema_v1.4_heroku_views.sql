

create or replace view public.job_data_raw_vw as
select draw.job_data_raw_id, 
   draw.job_execution_id,  
   draw.target_cwiki, 
   draw.activity_id,
   draw.assay_id,
   draw.target_id,
   draw.molecule_id,
   draw.cs_id,
   draw.relation,
   draw.standard_units,
   draw.standard_value,
   draw.activity_type,
   draw.inchi,
   draw.inchikey,
   draw.smiles,
   draw.ro5violations,
   draw.target_organism,
   draw.assay_organism,
   draw.pmid,
   draw.compound_cwiki,
   draw.full_mwt,
   draw.compound_pref_label,
   draw.activity_value,
   draw.target_pref_label,
   draw.assay_description,
   draw.target_title
FROM public.job_data_raw draw;


CREATE OR REPLACE VIEW public.job_data_filtered_vw AS 
 SELECT draw.job_data_raw_id, draw.job_execution_id,  
   draw.target_cwiki, 
   draw.activity_id,
   draw.assay_id,
   draw.target_id,
   draw.molecule_id,
   draw.cs_id,
   draw.relation,
   draw.standard_units,
   draw.standard_value,
   draw.activity_type,
   draw.inchi,
   draw.inchikey,
   draw.smiles,
   draw.ro5violations,
   draw.target_organism,
   draw.assay_organism,
   draw.pmid,
   draw.compound_cwiki,
   draw.full_mwt,
   draw.compound_pref_label,
   draw.activity_value,
   draw.target_pref_label,
   draw.assay_description,
   draw.target_title 
   FROM job_data_raw draw, job_data_filtered dfilt, job_execution jexec, job j, job_filtering jf, job_filtering_filter jff
  WHERE draw.job_data_raw_id = dfilt.job_data_raw_id AND dfilt.job_execution_id = jexec.job_execution_id AND jexec.job_id = j.job_id 
  AND j.job_filtering_id = jf.job_filtering_id AND jf.job_filtering_id = jff.job_filtering_id AND dfilt.filter_id = jff.filter_id
  GROUP BY draw.job_data_raw_id
 HAVING bool_and(dfilt.filter_passed) = true;

CREATE OR REPLACE VIEW public.job_data_filtered_vw_norm as 
select * 
from job_data_filtered_vw 
where (
	(activity_type='IC50' and standard_units='nM') or 
	(activity_type='Activity' and standard_units='nM') or 
	(activity_type='Inhibition' and standard_units='%') or
	(activity_type='Ki' and standard_units='nM')
);

--create or replace view public.job_execution_stats_raw as
--select job_execution_id,count(distinct activity_id) activities_raw, count(distinct molecule_id) compounds_raw
--from job_data_raw
--group by job_execution_id;

--create or replace view public.job_execution_stats_filtered as
--select job_execution_id,count(distinct activity_id) activities_filtered, count(distinct molecule_id) compounds_filtered
--from job_data_filtered_vw
--group by job_execution_id;

--v1.3
create or replace view public.job_execution_stats_raw as
select job_execution_id,count(distinct job_data_raw_id) activities_raw, count(distinct smiles) compounds_raw
from job_data_raw
group by job_execution_id;

create or replace view public.job_execution_stats_filtered as
select job_execution_id,count(distinct job_data_raw_id) activities_filtered, count(distinct smiles) compounds_filtered
from job_data_filtered_vw
group by job_execution_id;
--fv1.3

create or replace view public.job_execution_stats as 
select je.job_execution_id,jsr.activities_raw, jsr.compounds_raw, jsf.activities_filtered,jsf.compounds_filtered
from (job_execution je left join job_execution_stats_raw jsr on (je.job_execution_id= jsr.job_execution_id)) left join job_execution_stats_filtered jsf on (je.job_execution_id = jsf.job_execution_id);

create or replace view public.job_execution_vw as
SELECT t1.job_filtering_id,t1.job_description,t1.job_id,
	t2.job_filtering_description,t3.job_extraction_id,t3.job_extraction_description,
--	t4.job_execution_init_date,t4.job_execution_finish_extraction_date,t4.job_execution_finish_filtering_date, 
	CASE WHEN t4.job_execution_status='Ok' THEN to_char(t4.job_execution_finish_filtering_date, 'YYYYMMDD HH:MI')
            ELSE  to_char(t4.job_execution_init_date, 'YYYYMMDD HH24:MI')
        END as status_date,
	t4.job_execution_id,t4.job_execution_status,
	coalesce(stats.activities_raw,0) activities_raw,
	coalesce(stats.compounds_raw,0) compounds_raw,
	coalesce(stats.activities_filtered,0) activities_filtered,
	coalesce(stats.compounds_filtered,0) compounds_filtered
FROM job_extraction t3,job t1,job_filtering t2 , job_execution t4,   job_execution_stats stats
where 	
	t1.job_filtering_id = t2.job_filtering_id and t1.job_extraction_id = t3.job_extraction_id and t1.job_id = t4.job_id and
	 stats.job_execution_id=t4.job_execution_id;

--insert into job_execution_vw_mater select * from job_execution_vw
--truncate table job_execution_vw_mater
	 

--drop view job_data_statistics_base_vw
CREATE OR REPLACE VIEW public.job_data_statistics_base_vw AS 
 SELECT draw.job_data_raw_id, draw.job_execution_id,
     draw.activity_id, draw.molecule_id,  draw.smiles,
     jff.curation_order,dfilt.filter_passed   
   FROM job_data_raw draw, job_data_filtered dfilt, job_execution jexec, job j, job_filtering jf, job_filtering_filter jff
  WHERE draw.job_data_raw_id = dfilt.job_data_raw_id AND dfilt.job_execution_id = jexec.job_execution_id AND jexec.job_id = j.job_id 
  AND j.job_filtering_id = jf.job_filtering_id AND jf.job_filtering_id = jff.job_filtering_id AND dfilt.filter_id = jff.filter_id 
ORDER by draw.job_data_raw_id;

--drop view job_data_filtered_vw_averaged
CREATE OR REPLACE VIEW public.job_data_filtered_vw_averaged AS 
 SELECT dfilt.job_execution_id, dfilt.target_cwiki, dfilt.target_id, dfilt.cs_id, dfilt.activity_type,dfilt.standard_units, dfilt.inchi, dfilt.smiles, count(dfilt.cs_id) AS activities_count, median(dfilt.standard_value) AS activity_value_median, string_agg(dfilt.pmid, ',') as pmids
   FROM job_data_filtered_vw dfilt
  GROUP BY dfilt.job_execution_id, dfilt.target_cwiki, dfilt.target_id, dfilt.cs_id, dfilt.activity_type,dfilt.standard_units, dfilt.inchi, dfilt.smiles;

-- drop view job_data_raw_vw_averaged
CREATE OR REPLACE VIEW public.job_data_raw_vw_averaged AS 
 SELECT draw.job_execution_id, draw.target_cwiki, draw.target_id, draw.cs_id, draw.activity_type,draw.standard_units, draw.inchi, draw.smiles, count(draw.cs_id) AS activities_count, median(draw.standard_value) AS activity_value_median, string_agg(draw.pmid, ',') as pmids
   FROM job_data_raw draw
  GROUP BY draw.job_execution_id, draw.target_cwiki, draw.target_id, draw.cs_id, draw.activity_type,draw.standard_units, draw.inchi, draw.smiles;

