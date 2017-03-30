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

Ext.define('AM.view.job.List', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.joblist',
	title : 'Jobs',
	layout : {
		type : 'vbox',
		pack : 'start',
		align : 'stretch'
	},
	autoScroll : false,
	items : [ {
		xtype : 'panel',
		layout : {
			type : 'hbox',
			pack : 'start',
			align : 'left'
		},
		autoScroll : false,
		items : [
		{
			xtype : "button",
			text : "New",
			margin : '5 5 5 5',
		}, {
			xtype : "button",
			text : "Execute",
			margin : '5 5 5 5',
		}, {
			xtype : "button",
			text : "Delete",
			margin : '5 5 5 5',
		}, {
			xtype : "button",
			text : "NewProtocol",
			margin : '5 5 5 5',
		} ]
	}, {
		xtype : 'jobgrid',
		flex : 1
	}

	]
});