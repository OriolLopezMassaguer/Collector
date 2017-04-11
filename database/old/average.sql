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

drop view job_data_filtered_vw_averaged;
CREATE OR REPLACE VIEW job_data_filtered_vw_averaged  AS 
select 
   dfilt.job_execution_id,  
   dfilt.target_cwiki, 
   dfilt.target_id,
   dfilt.cs_id,
   dfilt.activity_type,
   dfilt.inchi,
   dfilt.smiles,
   count(dfilt.cs_id) activities_count,
   -- avg(standard_value),
   median(standard_value) activity_value_median 
from job_data_filtered_vw  dfilt
group by    
   dfilt.job_execution_id, 
   dfilt.target_cwiki, 
   dfilt.target_id,
   dfilt.cs_id,
   dfilt.activity_type,
   dfilt.inchi,
   dfilt.smiles;

drop view job_data_raw_vw_averaged;
CREATE OR REPLACE VIEW job_data_raw_vw_averaged AS 
select 
   draw.job_execution_id,  
   draw.target_cwiki, 
   draw.target_id,
   draw.cs_id,
   draw.activity_type,
   draw.inchi,
   draw.smiles,
   count(draw.cs_id) activities_count,
   --avg(standard_value),
   median(standard_value) activity_value_median
from job_data_raw draw
group by    
   draw.job_execution_id, 
   draw.target_cwiki, 
   draw.target_id,
   draw.cs_id,
   draw.activity_type,
   draw.inchi,
   draw.smiles;   


-- select * from job_data_filtered_vw 
-- where cs_id='http://rdf.chemspider.com/4911'
-- and job_execution_id=15
 