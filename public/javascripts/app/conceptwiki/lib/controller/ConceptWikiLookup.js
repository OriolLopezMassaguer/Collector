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

Ext.define('CW.controller.ConceptWikiLookup', {
    extend:'Ext.app.Controller',
    models: ['CW.model.ConceptWikiLookup'],
    views:['CW.view.ConceptWikiLookup'],
    
    init:function () {
        this.control({
            'conceptWikiLookup':{
                afterrender:this.prepProxy
            }
        });
    },
    select: function (field, value) {
    	console.log('Selection: '+field + ' '+ value);    	
    },

     // Fires when the box is rendered the first time
     prepProxy:function (cw_dropdown_view) {
    	console.log(cw_dropdown_view.fieldLabel)
    	console.log('Init uuid: '+cw_dropdown_view.cwTagUuid);
        cw_dropdown_view.store.proxy.extraParams = {uuid: cw_dropdown_view.cwTagUuid,branch:3, limit: 100}; // Ojo parche OLM
       
    },
    
   
    setConcept:function (concept_url, cw_lookup) {
     
	  console.log('CW.controller.ConceptWikiLookup: setConcept()');
      var concept_uuid = concept_url.match(/http:\/\/www.conceptwiki.org\/concept\/([a-f0-9\-]+)/)[1];
      var store = Ext.create('Ext.data.Store', {
        model: 'CW.model.ConceptWikiLookup',
        proxy: {
          type: 'jsonp',
          url: CW.config.Settings.getConceptUrl,
          reader: Ext.create('CW.helper.ConceptWikiJSONGetReader')
      }
      });
      store.load({
          params: {'uuid': concept_uuid },
          callback:function (records, operation, success) {
              if (success) {
                console.log("Success",records[0]);
                cw_lookup.setValue(records[0]);
              }
              else {
              
              }
          }
      },this );
    }
})
;


                