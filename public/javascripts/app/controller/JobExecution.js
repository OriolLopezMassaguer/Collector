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

Ext.define('AM.controller.JobExecution', {
    extend: 'Ext.app.Controller',

    stores: ['JobExecution','JobDataRAW','JobDataFiltered','ExecutionStatistics','ExecutionStatisticsHistogram'],
    models: ['JobExecution'],
    views: ['jobexecution.Grid','jobexecution.List'],
    
	init: function() {
		this.control({
			//'jobexecutiongrid': {  itemdblclick: this.filterJob},
			'jobexecutiongrid': {  itemclick: this.filterJob},
			'jobexecutionlist button[text=View in CBN]': { click: this.viewData},
			'jobexecutionlist button[text=Delete]': { click: this.deleteJob}
		});
	},
	
	putJobExecutionId: function(grid, record) {
		console.log("Putting job execution id");
	    var fieldjobexecutionid = Ext.ComponentQuery.query('#jobexecutionid');
	    console.log(fieldjobexecutionid);
	    fieldjobexecutionid[0].setValue(record.get('job_execution_id'));		
	},
	
	filterJob: function(grid, record) {
		console.log("Filter Job Execution");
		console.log("Job execution id:" +record.get('job_execution_id'));
		
	    var dataPanel = Ext.ComponentQuery.query('#datapanel');
	    console.log("Data panel"+dataPanel+ " show!");
	    dataPanel[0].show();
	    dataPanel[0].setActiveTab(0);
        
        var jc=this.getExecutionStatisticsStore();
        //console.log("Chart: "+jc);
        jc.clearFilter();
        jc.filter("job_execution_id", record.get('job_execution_id'));
        jc.load();

        var jeh=this.getExecutionStatisticsHistogramStore();
        //console.log("Histogram: "+jeh);
        jeh.clearFilter();
        jeh.filter("job_execution_id", record.get('job_execution_id'));
        jeh.load();
        
		var newjobform = Ext.ComponentQuery.query('#newjobform');
		//console.log(newjobform[0]);
		newjobform[0].hide();
		
		var protocolgrid = Ext.ComponentQuery.query('#protocolgrid');
		//console.log(protocolgrid[0]);
		protocolgrid[0].hide();
		
  
	    
	    var fieldjobexecutionid = Ext.ComponentQuery.query('#jobexecutionid');
	    //console.log(fieldjobexecutionid);
	    fieldjobexecutionid[0].setValue(record.get('job_execution_id'));
	    
	    var je=this.getJobDataRAWStore();
        je.clearFilter();
        je.filter("job_execution_id", record.get('job_execution_id'));
        je.load();
        var jf=this.getJobDataFilteredStore();
        jf.clearFilter();
        jf.filter("job_execution_id", record.get('job_execution_id'));
        jf.load();

        var chartStat = Ext.ComponentQuery.query('#chartStat');
	    
	    console.log("End job execution filter")
 
	},
	
	viewData: function(){
		console.log("View data in CBN");
		var fieldjobexecutionid = Ext.ComponentQuery.query('#jobexecutionid');
	    Ext.Ajax.request({
	        url: AM.config.Settings.hostAppServer+'/data/jobcbnlinkout/'+fieldjobexecutionid[0].getValue(),	        
	        success: function(response, opts) {
	        	console.log('CBN link!');	        	
	            var obj = Ext.decode(response.responseText)
	            console.log(response.responseText)
	            console.log(obj)
	            console.log(obj.url)
	            window.open(obj.url)	            
	        },
	        failure: function(response, opts) {
	            console.log('server-side failure with status code ' + response.status);
	            
	        }
	    });
	},

	deleteJob: function() {
	        var fieldjobexecutionid = Ext.ComponentQuery.query('#jobexecutionid');
		    console.log('Deleting job execution'+fieldjobexecutionid[0].getValue());
		    var jobexecutionstore= Ext.data.StoreManager.lookup('JobExecution');
		    Ext.Ajax.request({
		        url: AM.config.Settings.hostAppServer+'/data/jobexecdelete/'+fieldjobexecutionid[0].getValue(),		        
		        success: function(response, opts) {
		        	console.log('Delete job execution!');
		            var obj = Ext.decode(response.responseText);
		            console.dir(obj);
		    		//jobexecutionstore.load();
		        },
		        failure: function(response, opts) {
		            console.log('server-side failure with status code ' + response.status);
		    		jobexecutionstore.load();
		        }
		    });
		    //setTimeout(function(){jobexecutionstore.load();},30000);	
   }
     
});

