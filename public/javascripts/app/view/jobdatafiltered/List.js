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

Ext
		.define(
				'AM.view.jobdatafiltered.List', 
				{
					extend : 'Ext.panel.Panel',
					alias : 'widget.jobdatafiltered',
					layout : {
						type : 'vbox',
						pack : 'start',
						align : 'stretch'
					},
					autoScroll : true,
					items : [
							{
								xtype : 'panel',
								layout : {
									type : 'hbox',
									pack : 'start',
									align : 'left'
								},
								margin : '5 5 5 5',
								autoScroll : true,
								items : [
										{
											xtype : 'fieldcontainer',
											// fieldLabel : 'Size',
											defaultType : 'radiofield',
											layout : 'hbox',
											margin : '5 5 5 5',
											items : [ {
												boxLabel : 'Activities',
												name : 'downloadtype',
												inputValue : 'Activities',
												margin : '5 5 5 5',
												id : 'Activities2',
												checked: true
											}, {
												boxLabel : 'Compounds',
												name : 'downloadtype',
												inputValue : 'Compounds',
												margin : '5 5 5 5',
												id : 'Compounds2',
											}, ]
										},
										{
											   xtype:'combo',
											   fieldLabel:'Activity Type',
											   name:'activity',
											   valueField: 'activity',
											   queryMode:'local',
											   value: 'All',
											   store:['All','Potency','AC50','IC50','Inhibition','EC50','Activity','Ki'],
											   displayField:'activity',
											   autoSelect:true,
											   forceSelection:true,
											   id: 'activity2'
										},
										{
											xtype : 'button',
											text : 'Download as SDF',
											margin : '5 5 5 5',
											handler : function() {

												console.log('Downloading SDF');
												var fieldjobexecutionid = Ext.ComponentQuery
														.query('#jobexecutionid');
												console
														.log(fieldjobexecutionid[0]
																.getValue())
																
												var activity = Ext.ComponentQuery.query('#activity2');
												console.log('ActivityType');
												console.log(activity);
												console.log(activity[0].getValue());
												var activityType = activity[0].getValue();
												
												
												if (Ext.getCmp('Activities2').getValue()) {
													window.open( '/data/jobdatafilteredsdf/' + fieldjobexecutionid[0].getValue() +'?activityType='+ activityType);
												};

												if (Ext.getCmp('Compounds2').getValue()) {
													window.open( '/data/jobdatafilteredsdfag/' + fieldjobexecutionid[0].getValue() +'?activityType='+ activityType);
												};
																

											}
										},
										{
											xtype : 'button',
											text : 'Download as CSV',
											margin : '5 5 5 5',
											handler : function() {
												console.log('Downloading CSV');
												var fieldjobexecutionid = Ext.ComponentQuery
														.query('#jobexecutionid');
												
												
												var activity = Ext.ComponentQuery.query('#activity2');
												
												console.log('ActivityType');
												console.log(activity);
												console.log(activity[0].getValue());
												var activityType = activity[0].getValue();
												if (Ext.getCmp('Activities2').getValue()) {
													window.open( '/data/jobdatafilteredcsv/' + fieldjobexecutionid[0].getValue() +'?activityType='+ activityType);
												};

												if (Ext.getCmp('Compounds2').getValue()) {
													window.open( '/data/jobdatafilteredcsvag/' + fieldjobexecutionid[0].getValue() +'?activityType='+ activityType);
												};
											}
										}
//										{
//											xtype : 'button',
//											text : 'View in CBN',
//											margin : '5 5 5 5',
//											handler : function() {											
//												console.log("View data in CBN");
//												var fieldjobexecutionid = Ext.ComponentQuery.query('#jobexecutionid');
//											    Ext.Ajax.request({
//											        //url: AM.config.Settings.hostAppServer+'/data/jobcbnlinkout/'+fieldjobexecutionid[0].getValue(),
//											        url: '/data/jobcbnlinkout/'+fieldjobexecutionid[0].getValue(),
//											        success: function(response, opts) {
//											        	console.log('CBN link!');	        	
//											            var obj = Ext.decode(response.responseText)
//											            console.log(response.responseText)
//											            console.log(obj)
//											            console.log(obj.url)
//											            window.open(obj.url)	            
//											        },
//											        failure: function(response, opts) {
//											            console.log('server-side failure with status code ' + response.status);
//											        }
//											    });
//											},
//										}
										]
							},

							{
								xtype : 'jobdatafilteredgrid',
								flex : 1

							}

					]
				});