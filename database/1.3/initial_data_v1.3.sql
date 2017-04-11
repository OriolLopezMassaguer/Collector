----------------------------
-- Curation engine ---------
----------------------------
insert into job_filtering (job_filtering_description) VALUES ('No filtering'); --1
insert into job_filtering (job_filtering_description) VALUES ('LipRo5/Atom Validation + Activity'); --2
insert into job_filtering (job_filtering_description) VALUES ('LipRo5/Atom Validation + IC50'); --3
insert into job_filtering (job_filtering_description) VALUES ('LipRo5/Atom Validation + Inhibition'); --4
insert into job_filtering (job_filtering_description) VALUES ('LipRo5/Atom Validation + Ki'); --5
insert into job_filtering (job_filtering_description) VALUES ('LipRo5/Atom Validation'); --6

insert into filter (filter_description, filter_class) VALUES ('NoFiltering','es.imim.phi.collector.compounds.Nofilter'); --1
insert into filter (filter_description, filter_class) VALUES ('LipinskiRo5','es.imim.phi.collector.compounds.CompoundFilterRuleOf5'); --2
-- insert into filter (filter_id,filter_description, filter_class) VALUES (3,'Corina str. gen.','es.imim.phi.collector.compounds.CompoundFilterCorina'); --3
select nextval(pg_get_serial_sequence('filter', 'filter_id'));
insert into filter (filter_description, filter_class) VALUES ('ValidateAtoms','es.imim.phi.collector.compounds.CompoundFilterValidAtoms'); --4
insert into filter (filter_description, filter_class) VALUES ('Activity','es.imim.phi.collector.compounds.CompoundFilterActivity'); --5
insert into filter (filter_description, filter_class) VALUES ('IC50','es.imim.phi.collector.compounds.CompoundFilterIC50'); --6
insert into filter (filter_description, filter_class) VALUES ('Inhibition','es.imim.phi.collector.compounds.CompoundFilterInhibition'); --7
insert into filter (filter_description, filter_class) VALUES ('Ki','es.imim.phi.collector.compounds.CompoundFilterKi'); --8

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (1,1,1);

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,5,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,2,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,4,3); 

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,6,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,2,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,4,3); 

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,7,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,2,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,4,3); 

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (5,8,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (5,2,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (5,4,3);

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (6,2,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (6,4,2);  

--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('target_cwiki','STRING','target_cwiki');	
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('compound_cwiki','STRING','compound_cwiki');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('activity_id','STRING','activity_id');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('assay_id','STRING','assay_id');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('target_id','STRING','target_id');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('molecule_id','STRING','molecule_id');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('cs_id','STRING','cs_id');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('relation','STRING','relation');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('standard_units','STRING','standard_units');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('standard_value','FLOAT','standard_value');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('activity_value','FLOAT','activity_value');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('activity_type','STRING','activity_type');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('inchi','STRING','inchi');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('inchikey','STRING','inchikey');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('smiles','STRING','smiles');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('ro5violations','FLOAT','ro5violations');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('target_organism','STRING','target_organism');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('assay_organism','STRING','assay_organism');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('pmid','STRING','pmid');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('full_mwt','FLOAT','full_mwt');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('compound_pref_label','STRING','compound_pref_label');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('target_pref_label','STRING','target_pref_label');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('assay_description','STRING','assay_description');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('target_title','STRING','target_title');
--INSERT INTO job_parameter (job_parameterSQL,job_parameterSQLType,job_parameterLDA) VALUES ('job_execution_id','INTEGER','job_execution_id');


