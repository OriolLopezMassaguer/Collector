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

Ext.define('CW.model.ConceptWikiLookup', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'match', mapping: 'match', type: 'string' },
        { name: 'uuid', mapping: 'uuid', type: 'string' },
        { name: 'ops_uri', mapping: 'ops_uri', type: 'string' },
        { name: 'pref_label', mapping: 'pref_labels', type:'string'},
        { name: 'alt_labels', mapping: 'alt_labels', type: 'string' },
        { name: 'uuid', mapping: 'uuid', type: 'string' },
        { name: 'concept_type_tags', mapping: 'uuid_tags', type:'string'},
        { name: 'pref_url', mapping: 'pref_url', type: 'string' },
    ],
    getSomething: function () {
        if (this.something == null) this.parseSomething();

        return this.something;
    },
    parseSomething: function () {
        this.something = new Array();
        for (var i = 0; i < this.data.something.length; i++) {
            var syn = this.data.tags[i];
            if (syn.length == 1)
                this.something.push(syn);
        }
    }
});
