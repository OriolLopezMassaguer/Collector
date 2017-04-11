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


INSERT INTO buckets (bucket, bucket_order) VALUES ('2.50', 2.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('3.00', 3.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('3.50', 3.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('4.00', 4.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('4.50', 4.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('5.00', 5.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('5.50', 5.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('6.00', 6.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('6.50', 6.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('7.00', 7.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('7.50', 7.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('8.00', 8.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('8.50', 8.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('9.00', 9.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('9.50', 9.50);
INSERT INTO buckets (bucket, bucket_order) VALUES ('10.00', 10.00);
INSERT INTO buckets (bucket, bucket_order) VALUES ('10.50', 10.50);

