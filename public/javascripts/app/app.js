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

Ext.Loader.setConfig({
	disableCaching : false,
	enabled : true,
	paths : {
		CW : 'js/app/conceptwiki/lib',
		AM : 'js/app'
	}
});

Ext.application({
	name : 'AM',
	appFolder : 'js/app',
	//requires : [ 'AM.config.Settings' ],
	controllers : [ 'AM.controller.Job', 'AM.controller.JobExecution',
			'AM.controller.JobDataRAW', 'AM.controller.JobDataFiltered',
			'AM.controller.ExecutionStatistics', 'AM.controller.JobForm',
			'AM.controller.ExecutionStatisticsHistogram',
			'AM.controller.Protocol', 'AM.controller.FilteringProtocol',
			'CW.controller.ConceptWikiLookup' ],
	launch : function() {

		console.log(CW.config.Settings.base_ops_uri);
		Ext.create('Ext.container.Viewport', {
			layout : 'anchor',
			items : [ {
				xtype : 'panel',
				//width : 1000,

				bodyStyle : {
					'background-color' : '#74AFAD',
				},
				layout : 'hbox',
				items : [ {
					xtype : 'image',
					//src : AM.config.Settings.hostAppServer + '/js/logo.png',
					src : '/assets/images/logo.png',
					id : 'logo',
					anchor : '100%',
					height : 98,
				} ]
			}, {
				xtype : 'panel',
				itemId : 'jobpanel',
				layout : {
					type : 'hbox',
					pack : 'start',
					align : 'stretch'
				},
				anchor : '100% 35%',
				items : [ {
					xtype : 'textfield',
					itemId : 'jobid',
					fieldLabel : 'Job Id',
					hidden : true,
				}, {
					xtype : 'joblist',
					flex : 1
				},
				{
					xtype : "jobexecutionlist",
					flex : 1
				}, ]
			}, {
				xtype : 'panel',
				anchor : '100% 50%',
				layout : {
					type : 'vbox',
					pack : 'start',
					align : 'stretch'
				},

				items : [ {
					xtype : 'textfield',
					itemId : 'jobexecutionid',
					fieldLabel : 'Job Execution Id',
					hidden : true
				}, {
					xtype : 'newjobform',
					title : 'New Job',
					itemId : 'newjobform',
					hidden : true
				}, {
					xtype : "protocolgrid",
					title : "Protocol",
					hidden : true,
					flex : 1,
				}, {
					closable : true,
					closeAction : 'hide',
					flex : 1,
					xtype : 'tabpanel',
					itemId : 'datapanel',
					hidden : true,
					items : [ {
						xtype : 'panel',
						title : "Statistics charts",
						layout : 'hbox',
						align : 'stretch',
						item : 'tabcharts',
						items : [ {
							xtype : "statisticschart",
							title : "Filtering statistics chart",
						// flex : 1
						}, {
							xtype : "statisticshistogram",
							title : "Activities histogram",
						// flex : 1
						},
						]
					}, {
						xtype : "statisticsgrid",
						title : "Filtering statistics grid",
					}, {
						xtype : "jobdatafiltered",
						title : "Filtered data",
					}, {
						xtype : "jobdataraw",
						title : "Extracted data",
					},

					]
				},

				]
			}

			]
		});
	}

});

