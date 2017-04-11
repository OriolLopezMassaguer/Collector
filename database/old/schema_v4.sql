-- Function: _final_median(anyarray)

-- DROP FUNCTION _final_median(anyarray);

CREATE OR REPLACE FUNCTION _final_median(anyarray)
   RETURNS float8 AS
$$ 
  WITH q AS
  (
     SELECT val
     FROM unnest($1) val
     WHERE VAL IS NOT NULL
     ORDER BY 1
  ),
  cnt AS
  (
    SELECT COUNT(*) AS c FROM q
  )
  SELECT AVG(val)::float8
  FROM 
  (
    SELECT val FROM q
    LIMIT  2 - MOD((SELECT c FROM cnt), 2)
    OFFSET GREATEST(CEIL((SELECT c FROM cnt) / 2.0) - 1,0)  
  ) q2;
$$
LANGUAGE sql IMMUTABLE;
 
CREATE AGGREGATE median(anyelement) (
  SFUNC=array_append,
  STYPE=anyarray,
  FINALFUNC=_final_median,
  INITCOND='{}'
);


drop table job_data_filtered cascade;
drop table job_data_raw cascade;
drop table job_execution cascade;
drop table job cascade;
drop table job_parameter_value cascade;
drop table job_extraction cascade;
drop table job_filtering_filter cascade;
drop table job_filtering cascade;
drop table job_parameter cascade;
drop table filter cascade;
drop table job_execution_vw_mater cascade;

create table job_extraction
(
	job_extraction_id serial PRIMARY KEY,
	job_extraction_description varchar(250)
);

create table job_parameter
(
	job_parameter_id serial PRIMARY KEY,
	job_parameterSPARQL varchar(250),
	job_parameterSPARQLType varchar(250),
	job_parameterSQL varchar(250),
	job_parameterSQLType varchar(250),
	job_parameterLDA varchar(250)
);

create table job_parameter_value 
(
	job_extraction_id integer references job_extraction (job_extraction_id) ON DELETE CASCADE,
	job_parameter_id integer references job_parameter (job_parameter_id) ON DELETE CASCADE,
	job_parameter_value varchar(250)
);


create table job_filtering
(
	job_filtering_id serial PRIMARY KEY,
	job_filtering_description varchar(250)
);



CREATE TABLE filter
 (
	filter_id serial PRIMARY KEY,
	filter_description varchar(200) DEFAULT NULL,
	filter_class varchar(200) DEFAULT NULL
);

create table job_filtering_filter
(
	job_filtering_id integer references job_filtering (job_filtering_id) ON DELETE CASCADE,
	filter_id integer references filter (filter_id) ON DELETE CASCADE,
	curation_order integer not null,
	PRIMARY KEY (job_filtering_id,filter_id)
);



create table job 
(
	job_id serial PRIMARY KEY,
	job_description varchar(250),
	job_extraction_id integer references job_extraction (job_extraction_id) ON DELETE CASCADE,
	job_filtering_id integer references job_filtering (job_filtering_id) ON DELETE CASCADE
);




create table job_execution 
(
 	job_execution_id serial PRIMARY KEY,
 	job_id integer references job (job_id) ON DELETE CASCADE,
 	job_execution_init_date timestamp,
 	job_execution_finish_extraction_date timestamp,
 	job_execution_finish_filtering_date timestamp,
 	job_execution_status varchar(10)
);


CREATE TABLE job_data_raw
 (
   job_data_raw_id serial,
   job_execution_id integer,   
   target_cwiki varchar(200) DEFAULT NULL,
   activity_id varchar(200) DEFAULT NULL,
   assay_id varchar(200) DEFAULT NULL,
   target_id varchar(200) DEFAULT NULL,
   molecule_id varchar(200) DEFAULT NULL,
   cs_id varchar(200) DEFAULT NULL,
   relation varchar(10),
   standard_units varchar(50),
   standard_value numeric(30,3),
   activity_type varchar(100),
   inchi varchar(2000),
   inchikey varchar(2000),
   smiles varchar(1000),
   ro5violations numeric(30,3),
   target_organism varchar(200),
   assay_organism varchar(200),
   pmid varchar(20),
   molecule_cwiki varchar(200) DEFAULT NULL,
   full_mwt numeric(30,3),
   compoundPrefLabel varchar(10000),
   PRIMARY KEY (job_data_raw_id),
   FOREIGN KEY (job_execution_id) references job_execution (job_execution_id) ON DELETE CASCADE
   
);

CREATE INDEX job_execution_id_raw ON job_data_raw (job_execution_id);


CREATE TABLE job_data_filtered
 (
   job_filtering_id integer,
   filter_id integer,
   job_execution_id integer,
   job_data_raw_id integer,
   filter_passed boolean,
   PRIMARY KEY (job_filtering_id, filter_id,job_data_raw_id),
   FOREIGN KEY (job_filtering_id, filter_id) REFERENCES job_filtering_filter (job_filtering_id, filter_id),
   FOREIGN KEY (job_data_raw_id) REFERENCES job_data_raw (job_data_raw_id)  ON DELETE CASCADE,
   FOREIGN KEY (job_execution_id) REFERENCES job_execution (job_execution_id) ON DELETE CASCADE
   --INDEX (job_execution_id)
);

CREATE INDEX job_execution_id_filt ON job_data_filtered (job_execution_id);




CREATE OR REPLACE VIEW job_data_filtered_vw AS 
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
   draw.molecule_cwiki,
   draw.full_mwt,
   draw.compoundPrefLabel 
   FROM job_data_raw draw, job_data_filtered dfilt, job_execution jexec, job j, job_filtering jf, job_filtering_filter jff
  WHERE draw.job_data_raw_id = dfilt.job_data_raw_id AND dfilt.job_execution_id = jexec.job_execution_id AND jexec.job_id = j.job_id 
  AND j.job_filtering_id = jf.job_filtering_id AND jf.job_filtering_id = jff.job_filtering_id AND dfilt.filter_id = jff.filter_id
  GROUP BY draw.job_data_raw_id
 HAVING bool_and(dfilt.filter_passed) = true;

CREATE OR REPLACE VIEW job_data_filtered_vw_norm as 
select * 
from job_data_filtered_vw 
where (
	(activity_type='IC50' and standard_units='nM') or 
	(activity_type='Activity' and standard_units='nM') or 
	(activity_type='Inhibition' and standard_units='%') or
	(activity_type='Ki' and standard_units='nM')
);

create or replace view job_execution_stats_raw as
select job_execution_id,count(distinct activity_id) activities_raw, count(distinct molecule_id) compounds_raw
from job_data_raw
group by job_execution_id;

create or replace view job_execution_stats_filtered as
select job_execution_id,count(distinct activity_id) activities_filtered, count(distinct molecule_id) compounds_filtered
from job_data_filtered_vw
group by job_execution_id;

create or replace view job_execution_stats as 
select je.job_execution_id,jsr.activities_raw, jsr.compounds_raw, jsf.activities_filtered,jsf.compounds_filtered
from (job_execution je left join job_execution_stats_raw jsr on (je.job_execution_id= jsr.job_execution_id)) left join job_execution_stats_filtered jsf on (je.job_execution_id = jsf.job_execution_id);

create or replace view job_execution_vw as
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
	 
CREATE TABLE job_execution_vw_mater
(
  job_filtering_id integer,
  job_description character varying(250),
  job_id integer references job (job_id) ON DELETE CASCADE,
  job_filtering_description character varying(250),
  job_extraction_id integer,
  job_extraction_description character varying(250),
  status_date text,
  job_execution_id integer,
  job_execution_status character varying(10),
  activities_raw bigint,
  compounds_raw bigint,
  activities_filtered bigint,
  compounds_filtered bigint,
  FOREIGN KEY (job_execution_id) references job_execution (job_execution_id) ON DELETE CASCADE
);

--drop view job_data_statistics_base_vw
CREATE OR REPLACE VIEW job_data_statistics_base_vw AS 
 SELECT draw.job_data_raw_id, draw.job_execution_id,
     draw.activity_id, draw.molecule_id,  
     jff.curation_order,dfilt.filter_passed   
   FROM job_data_raw draw, job_data_filtered dfilt, job_execution jexec, job j, job_filtering jf, job_filtering_filter jff
  WHERE draw.job_data_raw_id = dfilt.job_data_raw_id AND dfilt.job_execution_id = jexec.job_execution_id AND jexec.job_id = j.job_id 
  AND j.job_filtering_id = jf.job_filtering_id AND jf.job_filtering_id = jff.job_filtering_id AND dfilt.filter_id = jff.filter_id 
ORDER by draw.job_data_raw_id;

--drop view job_data_filtered_vw_averaged
CREATE OR REPLACE VIEW job_data_filtered_vw_averaged AS 
 SELECT dfilt.job_execution_id, dfilt.target_cwiki, dfilt.target_id, dfilt.cs_id, dfilt.activity_type,dfilt.standard_units, dfilt.inchi, dfilt.smiles, count(dfilt.cs_id) AS activities_count, median(dfilt.standard_value) AS activity_value_median, string_agg(dfilt.pmid, ',') as pmids
   FROM job_data_filtered_vw dfilt
  GROUP BY dfilt.job_execution_id, dfilt.target_cwiki, dfilt.target_id, dfilt.cs_id, dfilt.activity_type,dfilt.standard_units, dfilt.inchi, dfilt.smiles;

-- drop view job_data_raw_vw_averaged
CREATE OR REPLACE VIEW job_data_raw_vw_averaged AS 
 SELECT draw.job_execution_id, draw.target_cwiki, draw.target_id, draw.cs_id, draw.activity_type,draw.standard_units, draw.inchi, draw.smiles, count(draw.cs_id) AS activities_count, median(draw.standard_value) AS activity_value_median, string_agg(draw.pmid, ',') as pmids
   FROM job_data_raw draw
  GROUP BY draw.job_execution_id, draw.target_cwiki, draw.target_id, draw.cs_id, draw.activity_type,draw.standard_units, draw.inchi, draw.smiles;


	 
