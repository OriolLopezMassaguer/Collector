﻿----------------------------
-- Curation engine ---------
----------------------------

insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (1,'LipRo5/Corina Structure/Atom Validation + Activity');
insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (2,'LipRo5/Corina Structure/Atom Validation + IC50');
insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (3,'LipRo5/Corina Structure/Atom Validation + Inhibition');
insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (4,'LipRo5/Corina Structure/Atom Validation + Ki');

insert into filter (filter_id,filter_description, filter_class) VALUES (1,'Lipinski Ro5','es.imim.cadd.openphacts.etox.scala.CompoundFilterRuleOf5');
insert into filter (filter_id,filter_description, filter_class) VALUES (2,'Corina str. gen.','es.imim.cadd.openphacts.etox.scala.CompoundFilterCorina');
insert into filter (filter_id,filter_description, filter_class) VALUES (3,'Validate Atoms','es.imim.cadd.openphacts.etox.scala.CompoundFilterValidAtoms');
insert into filter (filter_id,filter_description, filter_class) VALUES (4,'Activity','es.imim.cadd.openphacts.etox.scala.CompoundFilterActivity');
insert into filter (filter_id,filter_description, filter_class) VALUES (5,'IC50','es.imim.cadd.openphacts.etox.scala.CompoundFilterIC50');
insert into filter (filter_id,filter_description, filter_class) VALUES (6,'Inhibition','es.imim.cadd.openphacts.etox.scala.CompoundFilterInhibition');
insert into filter (filter_id,filter_description, filter_class) VALUES (7,'Ki','es.imim.cadd.openphacts.etox.scala.CompoundFilterKi');

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (1,4,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (1,1,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (1,3,3); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (1,2,4);

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,5,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,1,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,3,3); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (2,2,4);

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,6,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,1,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,3,3); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (3,2,4);

insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,7,1);
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,1,2); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,3,3); 
insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (4,2,4);

INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('target_cwiki','URI','target_cwiki','STRING');	
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('activity','URI','activity_id','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('assay','URI','assay_id','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('target','URI','target_id','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('molecule','URI','molecule_id','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('csid','URI','cs_id','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('relation','STRING','relation','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('standard_units','STRING','standard_units','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('standard_value','STRING','standard_value','FLOAT');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('activity_type','STRING','activity_type','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('inchi','STRING','inchi','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('inchikey','STRING','inchikey','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('smiles','STRING','smiles','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('ro5violations','STRING','ro5violations','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('target_organism','STRING','target_organism','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('assay_organism','STRING','assay_organism','STRING');
INSERT INTO job_parameter (job_parameterSPARQL,job_parameterSPARQLType,job_parameterSQL,job_parameterSQLType) VALUES ('pmid','STRING','pmid','STRING');

---------------------
-----  Job & Parameters
---------------------

insert into job_extraction (job_extraction_description) VALUES ('Potassium voltage-gated channel subfamily H member 2 (Homo sapiens): WP 3/4 Shortlist');
insert into job_extraction (job_extraction_description) VALUES ('ATP-binding cassette sub-family G member 2 (Homo sapiens): WP 3/4 Shortlist');
insert into job_extraction (job_extraction_description) VALUES ('Retinoic acid receptor alpha (Homo sapiens): WP 3/4 Shortlist');
insert into job_extraction (job_extraction_description) VALUES ('KCNQ1');

insert into job_parameter_value (job_extraction_id,job_parameter_id,job_parameter_value) VALUES (1,1,'http://www.conceptwiki.org/concept/d2a9dbd1-78fb-4f2f-adba-9be028a15bd1');
--d2a9dbd1-78fb-4f2f-adba-9be028a15bd1

insert into job_parameter_value (job_extraction_id,job_parameter_id,job_parameter_value) VALUES (2,1,'http://www.conceptwiki.org/concept/4f20b1a7-b0f6-437b-b23f-16d7ec6d96b5');
 --4f20b1a7-b0f6-437b-b23f-16d7ec6d96b5
 
insert into job_parameter_value (job_extraction_id,job_parameter_id,job_parameter_value) VALUES (3,1,'http://www.conceptwiki.org/concept/51dd31a8-1089-43e2-ba6c-5ce8e0a53165');
 --51dd31a8-1089-43e2-ba6c-5ce8e0a53165
-- targets ivan
-- Potassium voltage-gated channel subfamily H member 2 (Homo sapiens)
-- Voltage-dependent L-type calcium channel subunit alpha-1C (Homo sapiens)
-- KCNQ1 // Potassium voltage-gated channel subfamily KQT member 1 (Homo sapiens)

insert into job_parameter_value (job_extraction_id,job_parameter_id,job_parameter_value) VALUES (4,1,'http://www.conceptwiki.org/concept/4d5b75f1-2ab3-45af-8e44-e7b0b250ba25');
 
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt Activity',1,1);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt Activity',2,1);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt Activity',3,1);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt Activity',4,1);

insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt IC50',1,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt IC50',2,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt IC50',3,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt IC50',4,2);

insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt Inhibition',1,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt Inhibition',2,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt Inhibition',3,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt Inhibition',4,3);

insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt Ki',1,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt Ki',2,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt Ki',3,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt Ki',4,4);

-----------------------
-- otros tests
-----------------------

-- insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (5,'Activity');
-- insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (6,'IC50');
-- insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (7,'Inhibition');
-- insert into job_filtering (job_filtering_id,job_filtering_description) VALUES (8,'Ki');
-- insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel Activity filt Activity',1,5);
-- insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel Activity filt IC50',1,6);
-- insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel Activity filt Inhibition',1,7);
-- insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel Activity filt Ki',1,8);
-- insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (5,4,1);
-- insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (6,5,1);
-- insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (7,6,1);
-- insert into job_filtering_filter (job_filtering_id, filter_id,curation_order) VALUES (8,7,1);
