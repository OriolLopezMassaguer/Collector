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

Ext.define('AM.controller.JobForm', {
    extend: 'Ext.app.Controller',
    stores: ['Job'],
    //models: ['JobNew'],
    views: ['job.JobForm'], 
    
//	init : function() {
//		this.control({
//			'newjobform button[text=Save]' : {
//				click : this.newJob
//			},
//		});
//	},
//    
//	newjob: function()
//    
//	{
//		console.log("New job!!!");
//		var targetByNameLookup = Ext.ComponentQuery.query('#targetByNameLookup');
//		console.log("Target!!!"+targetByNameLookup[0].getValue());
//		console.log("Target!!!"+targetByNameLookup[0].rawValue);
//		var targetLabel = Ext.ComponentQuery.query('#targetlabel');
//		targetLabel[0].setValue(targetByNameLookup[0].rawValue);
//		var jobstore= this.getJobStore();
//		console.log("Job store"+jobstore);
//		//console.log(cw_dropdown_view.fieldLabel)
//		//console.log('Init uuid: '+cw_dropdown_view.cwTagUuid); 
//		var form = this.up('form').getForm();
//		form.submit({
//			url: '/data/newjob',
//			waitMsg: 'Sending the info...',
//            success: function(form, action) {
//            	Ext.Msg.alert('Success', 'Form submitted.');
//            },
//            failure: function(form, action) {
//            	Ext.Msg.alert('Failure', 'Form submitted.');
//            }
//			});
//	}
});



