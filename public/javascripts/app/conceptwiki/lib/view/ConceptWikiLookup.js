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

Ext.define('CW.view.ConceptWikiLookup', {
    extend:'Ext.form.ComboBox',
    alias:'widget.conceptWikiLookup',
    requires:[
        'CW.model.ConceptWikiLookup',
        'CW.helper.ConceptWikiJSONReader',
        'CW.store.ConceptWikiLookup'
    ],
    cwTagUuid: 'pleaseConfigure[cwConceptTagUuid:]',
    store: Ext.create('CW.store.ConceptWikiLookup'),
    // search boks configs
    forceSelection:true,
    allowBlank:false,
    typeAhead:true,
    typeAheadDelay: 50,
    queryDelay: 100,
    queryCaching: false,
    queryParam: 'q',
    queryMode:'remote',
    valueField:'ops_uri',
    displayField:'pref_label',
    name: 'ops_uri',  // can be overwritten in view config
    minChars:3,
    hideTrigger:true,
    forceSelection:true,
    allowBlank:false,
    typeAhead:true,
    emptyText:'Start typing...',
    margin:'5 5 5 5',
    width:700,
    fieldLabel: 'Overwrite this in config',
    //labelWidth:120,  // olm
    listConfig:{
        loadingText:'Searching...',
        emptyText:'No matching entities found.',
        getInnerTpl:function () {
            return '<p><span style="font-family: verdana; color: grey; "><small>Match: {match}</small></span><br/><b>{pref_label}</b> <a href="http://ops.conceptwiki.org/wiki/#/concept/{uuid}/view" target="_blank">(definition)</a></p>';
        }                                                                                                                                                                                        
    }
});
         