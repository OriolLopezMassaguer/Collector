---------------------
-----  Job & Parameters
---------------------

--insert into job_extraction (job_extraction_description) VALUES ('SDF Source'); --1
insert into job_extraction (job_extraction_description) VALUES ('Potassium voltage-gated channel subfamily H member 2 (Homo sapiens): WP 3/4 Shortlist'); --1
insert into job_extraction (job_extraction_description) VALUES ('ATP-binding cassette sub-family G member 2 (Homo sapiens): WP 3/4 Shortlist'); --2
insert into job_extraction (job_extraction_description) VALUES ('Retinoic acid receptor alpha (Homo sapiens): WP 3/4 Shortlist'); --3
insert into job_extraction (job_extraction_description) VALUES ('KCNQ1'); --4
insert into job_extraction (job_extraction_description) VALUES ('BSEP'); --5

insert into job_parameter_value (job_extraction_id,job_parameter_field,job_parameter_value) VALUES (1,'target_cwiki','http://www.conceptwiki.org/concept/d2a9dbd1-78fb-4f2f-adba-9be028a15bd1');
--d2a9dbd1-78fb-4f2f-adba-9be028a15bd1

insert into job_parameter_value (job_extraction_id,job_parameter_field,job_parameter_value) VALUES (2,'target_cwiki','http://www.conceptwiki.org/concept/4f20b1a7-b0f6-437b-b23f-16d7ec6d96b5');
 --4f20b1a7-b0f6-437b-b23f-16d7ec6d96b5
 
insert into job_parameter_value (job_extraction_id,job_parameter_field,job_parameter_value) VALUES (3,'target_cwiki','http://www.conceptwiki.org/concept/51dd31a8-1089-43e2-ba6c-5ce8e0a53165');
 --51dd31a8-1089-43e2-ba6c-5ce8e0a53165

insert into job_parameter_value (job_extraction_id,job_parameter_field,job_parameter_value) VALUES (4,'target_cwiki','http://www.conceptwiki.org/concept/4d5b75f1-2ab3-45af-8e44-e7b0b250ba25');
--BSEP
insert into job_parameter_value (job_extraction_id,job_parameter_field,job_parameter_value) VALUES (5,'target_cwiki','http://www.conceptwiki.org/concept/fdde9051-bbe7-4299-9dc7-ae3bc2389aff');
 
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt Activity',1,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt Activity',2,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt Activity',3,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt Activity',4,2);

insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt IC50',1,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt IC50',2,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt IC50',3,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt IC50',4,3);

insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt Inhibition',1,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt Inhibition',2,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt Inhibition',3,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt Inhibition',4,4);

insert into job (job_description,job_extraction_id,job_filtering_id) values ('Potassium voltage-gated channel filt Ki',1,5);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('ATP-binding cassette filt Ki',2,5);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('Retinoic acid receptor alpha filt Ki',3,5);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('KNCQ1 filt Ki',4,5);


insert into job (job_description,job_extraction_id,job_filtering_id) values ('BSEP filt Activity',5,2);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('BSEP filt IC50',5,3);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('BSEP filt Inhibition',5,4);
insert into job (job_description,job_extraction_id,job_filtering_id) values ('BSEP filt Ki',5,5);


