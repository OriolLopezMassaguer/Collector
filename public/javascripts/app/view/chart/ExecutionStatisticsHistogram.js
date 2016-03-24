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

Ext.define('AM.view.chart.ExecutionStatisticsHistogram', {
	itemId : 'chartStat',
	extend : "Ext.chart.Chart",
	alias : 'widget.statisticshistogram',
	store : 'ExecutionStatisticsHistogram',
	width : 900,
	height : 400,
	//autoScroll : true,
	autoRender : true,
	axes : [ {
		title : 'Activities',
		type : 'Numeric',
		position : 'left',
		fields : [ 'activities' ],
	 minimum : 0
	}, {
		title : 'pActivity',
		type : 'Category',
		position : 'bottom',
		fields : [ 'descriptor' ],
	} ],
	series : [ {
		type : 'column',
		axis : 'left',
		// gutter : 0,
		xField : 'descriptor',
		yField : 'activities',
	// label: { display: 'middle'}
	},

	]
});
