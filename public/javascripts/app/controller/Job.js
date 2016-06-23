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

Ext.define('AM.controller.Job', {
	extend : 'Ext.app.Controller',
	stores : [ 'Job', 'JobExecution' ],
	models : [ 'Job' ],
	views : [ 'job.List', 'job.Grid' ], 

	init : function() {
		this.control({
			'jobgrid' : {
				itemclick : this.filterJob
			},
			'joblist button[text=Execute]' : {
				click : this.executeJob
			},
			'joblist button[text=Delete]' : {
				click : this.deleteJob
			},
			'joblist button[text=New]' : {
				click : this.newJob
			},
			'joblist button[text=NewProtocol]' : {
				click : this.newProtocol
			},
		});
	},

	newProtocol : function() {
		console.log('New job ');
		var protocolgrid = Ext.ComponentQuery.query('#protocolgrid');
		console.log(protocolgrid[0]);
		protocolgrid[0].show();
		
		var newjobform = Ext.ComponentQuery.query('#newjobform');
		console.log(newjobform[0]);
		newjobform[0].hide();
	
	    var dataPanel = Ext.ComponentQuery.query('#datapanel');
	    console.log("Data panel"+dataPanel);
	    dataPanel[0].hide(); 
	},
	
	
	filterJob : function(grid, record) {
		console.log("Filter Job");
		var je = this.getJobExecutionStore();
		je.clearFilter();
		je.filter("job_id", record.get('job_id'));
		console.log(record.get('job_id'));
		je.load();
		var fieldjobid = Ext.ComponentQuery.query('#jobid');
		console.log(fieldjobid);
		fieldjobid[0].setValue(record.get('job_id'));
		
		var dataPanel = Ext.ComponentQuery.query('#datapanel');
	    dataPanel[0].hide();

	},

	executeJob : function() {
		console.log(" Execute!!!!");
		var je = this.getJobExecutionStore();
		var task = new Ext.util.DelayedTask(function(){
			console.log("Filter Job Delayed");	
			je.load();
			task.delay(10000);
		});
		
		task.delay(10000);
		
		var grid = Ext.ComponentQuery.query('#jobgrid')[0];
		console.log(grid);
		selectionModel = grid.getSelectionModel();

		console.log(selectionModel);

		selectedRecords = selectionModel.getSelection();
		myValue = selectedRecords[0].get('job_id');
		console.log(myValue);
		var jobexecutionstore= Ext.data.StoreManager.lookup('JobExecution');
		Ext.Msg.alert('Success', 'Job launched!');
		console.log(jobexecutionstore);
		jobexecutionstore.load();
		Ext.Ajax.request({
			//url : AM.config.Settings.hostAppServer+'/data/jobexec/' + myValue,
			url : '/data/jobexec/' + myValue,
			timeout : 0,
            success: function(response, opts) {
            	Ext.Msg.alert('Success', 'Job finished!');
            	console.log("Job finished!");
            	jobexecutionstore.load();
            },
//            failure: function(response, opts) {
//            	Ext.Msg.alert('FailureSuccess', 'Job failed. Contact administrator.');
//            	console.log("Job failed!");
//            	console.log(response.status);
//            	jobexecutionstore.load();
//            },
		});
		//setTimeout(function(){jobexecutionstore.load();},1000);		
		console.log('request');
	},

	deleteJob : function() {
		//console.log('Deleting job execution');
		var fieldjobid = Ext.ComponentQuery.query('#jobid');
		console.log('Deleting job execution'+fieldjobid[0].getValue());
		var jobstore= this.getJobStore();
		Ext.Ajax.request({
					//url : AM.config.Settings.hostAppServer+'/data/jobdelete/'				+ fieldjobid[0].getValue(),
					url : '/data/jobdelete/'+ fieldjobid[0].getValue(),							
					success : function(response, opts) {
						console.log("Delete JobSuccess!");
						var obj = Ext.decode(response.responseText);
						console.log(obj);
						jobstore.load();
					},
					failure : function(response, opts) {
						console.log('server-side failure with status code '
								+ response.status);
					}
				});
		
	},

	newJob : function() {
		console.log('New job ');
		var newjobform = Ext.ComponentQuery.query('#newjobform');
		console.log(newjobform[0]);
		newjobform[0].items.each( function(f) {
			  console.log(f);
			  f.setValue("");
		});
		newjobform[0].show();
	    var dataPanel = Ext.ComponentQuery.query('#datapanel');
	    console.log("Data panel"+dataPanel);
	    dataPanel[0].hide();

		var protocolgrid = Ext.ComponentQuery.query('#protocolgrid');
		console.log(protocolgrid[0]);
		protocolgrid[0].hide();
		
	}

});
