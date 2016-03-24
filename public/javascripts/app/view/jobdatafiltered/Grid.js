/*   
     Collector is a tool for obtaining bioactivity data from the Open PHACTS platform.
     Copyright (C) 2013 UPF
     Contributed by Manuel Pastor(manuel.pastor@upf.edu) and Oriol L. Massaguer(olopez@imim.es). 
 
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

Ext
		.define(
				'AM.view.jobdatafiltered.Grid',
				{
					extend : 'Ext.grid.Panel',
					alias : 'widget.jobdatafilteredgrid',
					store : 'JobDataFiltered',
					autoScroll : true,
					autoWidth: true,
					dockedItems : [ {
						xtype : 'pagingtoolbar',
						store : 'JobDataFiltered',
						dock : 'top'
					} ],
					listeners : {
						render : function(grid) {
							// grid.body.mask('Loading...');
							var store = this.getStore();
							console.log(store);
							store.load();
						},
						delay : 200
					},
					initComponent : function() {

						this.columns = [
								// {
								// header : 'job_data_raw_id',
								// dataIndex : 'job_data_raw_id',
								// flex : 1
								// },
								{
									header : 'job_execution_id',
									dataIndex : 'job_execution_id',
									flex : 1
								},
								// {
								// header : 'target_cwiki',
								// dataIndex : 'target_cwiki',
								// flex : 1,
								// renderer : function(val, meta, record) {
								// return '<a href="' + val
								// + '" target="_blank" >' + val
								// + '</a>';
								// }
								// },
								{
									header : 'compound_cwiki',
									dataIndex : 'compound_cwiki',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="' + val
												+ '" target="_blank" >' + val
												+ '</a>';
									}
								},
								{
									header : 'activity_id',
									dataIndex : 'activity_id',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="' + val
												+ '" target="_blank" >' + val
												+ '</a>';
									}
								},
								{
									header : 'assay_id',
									dataIndex : 'assay_id',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="' + val
												+ '" target="_blank" >' + val
												+ '</a>';
									}
								},
								{
									header : 'target_id',
									dataIndex : 'target_id',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="' + val
												+ '" target="_blank" >' + val
												+ '</a>';
									}
								},
								{
									header : 'cs_id',
									dataIndex : 'cs_id',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="'
												+ val.replace('rdf', 'www')
												+ '" target="_blank" >'
												+ val.replace('rdf', 'wwww')
												+ ' </a>';
									}
								},
								{
									header : 'cs_image',
									dataIndex : 'cs_id',
									width: 250,
									renderer : function(val, meta, record) {
										return '<img src="'
												+ 'http://ops.rsc.org/ImageHandler.ashx?CompoundID='
												+ val
														.replace(
																'http://ops.rsc.org/OPS',
																'') + '&w='
												+ '200' + '&h=' + '200' + '">'
												// + val.replace('rdf', 'wwww')
												+ '</img>';
									}

								},
								{
									header : 'molecule_id',
									dataIndex : 'molecule_id',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="' + val
												+ '" target="_blank" >' + val
												+ '</a>';
									}
								},
								{
									header : 'relation',
									dataIndex : 'relation',
									flex : 1
								},
								{
									header : 'standard_units',
									dataIndex : 'standard_units',
									flex : 1
								},
								{
									header : 'standard_value',
									dataIndex : 'standard_value',
									flex : 1
								},
								{
									header : 'activity_type',
									dataIndex : 'activity_type',
									flex : 1
								},
								{
									header : 'inchi',
									dataIndex : 'inchi',
									flex : 1
								},
								{
									header : 'inchikey',
									dataIndex : 'inchikey',
									flex : 1
								},
								{
									header : 'smiles',
									dataIndex : 'smiles',
									flex : 1
								},
								{
									header : 'ro5violations',
									dataIndex : 'ro5violations',
									flex : 1
								},
								{
									header : 'target_organism',
									dataIndex : 'target_organism',
									flex : 1
								},
								// {
								// header : 'assay_organism',
								// dataIndex : 'assay_organism',
								// flex : 1
								// },
								{
									header : 'pmid',
									dataIndex : 'pmid',
									flex : 1,
									renderer : function(val, meta, record) {
										return '<a href="' + val
												+ '" target="_blank" >' + val
												+ '</a>';
									}
								} ];

						this.callParent(arguments);
					}

				});