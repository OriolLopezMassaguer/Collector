/*   
     Collector is a tool for obtaining bioactivity data from the Open PHACTS platform.
     Copyright (C) 2013 UPF
     Contributed by Manuel Pastor(manuel.pastor@upf.edu) and Oriol López-Massaguer(oriol.lopez@upf.edu). 
 
    This file is part of Collector.

    Collector is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Collector is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Collector.  If not, see <http://www.gnu.org/licenses/>.
   
*/

Ext.define('AM.model.JobDataFiltered', {
	extend : 'Ext.data.Model',
	fields : [ {
		name : 'job_data_raw_id',
		type : 'int'
	}, {
		name : 'job_execution_id',
		type : 'int'
	}, 'target_cwiki', 'activity_id', 'assay_id', 'target_id', 'cs_id',
			'molecule_id', 'relation', 'standard_units', {
				name : 'standard_value',
				type : 'float'
			}, 'activity_type', 'inchi', 'inchikey', 'smiles', {
				name : 'ro5violations',
				type : 'int'
			},
			'target_organism', 'assay_organism', 'pmid' ]
});