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

Ext.define('AM.view.job.JobForm', {
	store : 'Job', 
	extend : 'Ext.form.Panel',
	alias : 'widget.newjobform',
	layout : 'vbox',
	padding : '5',
	align : 'left',
	title : 'Simple Form',
	bodyStyle : 'padding:5px 5px 5px 5px',
	closable: true,
	closeAction: 'hide',
	defaultType : 'textfield',
//	defaults : {
//		anchor : '100%'
//	},
	//buttonAlign : 'left',
	buttons : [
			{
				text : 'Save',
				margin : '5 5 5 5',
				handler : function() {
					var protocol_id = Ext.ComponentQuery.query('#protocol_id');
					//var targetByNameLookup = Ext.ComponentQuery.query('#targetByNameLookup');
					

					//console.log("Target!!! " + targetByNameLookup[0].getValue());
					//console.log("Target!!! " + targetByNameLookup[0].rawValue);
					//targetLabel[0].setValue(targetByNameLookup[0].rawValue);
					// console.log(cw_dropdown_view.fieldLabel)
                    // console.log('Init uuid: '+cw_dropdown_view.cwTagUuid);
					
					var targetId = Ext.ComponentQuery.query('#protein_id');
					var targetLabel = Ext.ComponentQuery.query('#targetlabel');
					targetLabel[0].setValue(targetId[0].rawValue);
					console.log("Target: " + targetId[0].getValue());
					console.log("Protocol: " + protocol_id[0].rawValue);

					

					var jobstore = Ext.data.StoreManager.lookup('Job');
					console.log("Job store" + jobstore);
					var form = this.up('form').getForm();
					form.submit({
<<<<<<< HEAD
						url : 'data/newjob',
=======
						url : '/collector/data/newjob',
>>>>>>> new search by text implementation
						waitMsg : 'Sending the info...',
						success : function(form, action) {
							Ext.Msg.alert('Success', 'Job submitted.');
							jobstore.load();
						},
						failure : function(form, action) {
							Ext.Msg.alert('Failure', 'Job submitted.');
							jobstore.load();
						}
					});
					var newjobform = Ext.ComponentQuery.query('#newjobform');
					console.log(newjobform[0]);
					newjobform[0].hide(); 
				}
			}, {
				text : 'Cancel',
				margin : '5 5 5 5',
				handler : function() {
					var newjobform = Ext.ComponentQuery.query('#newjobform');
					console.log(newjobform[0]);
					newjobform[0].hide();						
				}				
			} ],

	items : [ {
		fieldLabel : 'Job Description',
		name : 'job_description',
		margin : '5 5 5 5',
	}, {
		fieldLabel : 'Target label',
		name : 'target_label',
		itemId : 'targetlabel',
		hidden : true
	}, 
	
//	Ext.create('CW.view.ConceptWikiLookup', {
//		xtype : "conceptWikiLookup",
//		layout : 'vbox',
//		title : "Concept wiki",
//		fieldLabel : 'Protein name',
//		itemId : 'targetByNameLookup',
//		name : 'protein_uri',
//		cwTagUuid : 'eeaec894-d856-4106-9fa1-662b1dc6c6f1'
//	}), 
	
	Ext.create('Ext.form.ComboBox', {
        itemId : 'protein_id',
        name : 'id',
        fieldLabel : 'Choose Target',
        store : Ext.create('AM.store.FilteringTarget'),

        queryMode : 'remote',
        queryParam : 'protein_string',
        
        
        displayField : 'label',
        valueField : 'id',
        forceSelection : true,
        allowBlank : false,
        typeAhead : true,
        typeAheadDelay : 100,
        queryDelay : 100,
        queryCaching : false,
        width : 600,
        // labelWidth: 200,
        matchFieldWidth : true,
        minChars : 3,
        //hideTrigger : false,
        emptyText : 'Start typing...',
        fieldLabel : 'Target Name',
    }),
	
	Ext.create('Ext.form.ComboBox', {
		itemId : 'protocol_id',
		name : 'protocol_id',
		fieldLabel : 'Choose Protocol',
		store : Ext.create('AM.store.FilteringProtocol'),
		queryMode : 'remote',
		queryParam : 'protocol_string',
		displayField : 'job_filtering_description',
		valueField : 'job_filtering_id',
		forceSelection : true,
		allowBlank : false,
		typeAhead : true,
		typeAheadDelay : 100,
		queryDelay : 100,
		queryCaching : false,
		width : 600,
		// labelWidth: 200,
		matchFieldWidth : true,
		minChars : 3,
		//hideTrigger : false,
		emptyText : 'Start typing...',
		fieldLabel : 'Protocol Name',
	})

	]

});