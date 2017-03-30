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

Ext.define('AM.view.chart.ExecutionStatisticsChart', {
	itemId : 'chart',
	extend : "Ext.chart.Chart",
	alias : 'widget.statisticschart',
    width : 300,
	height : 300,
	//autoScroll : true,
	autoRender : true,
	legend : {
		position : 'top'
	},
	store : 'ExecutionStatistics',
	axes : [ {
		title : 'Activities / Compounds',
		type : 'Numeric',
		position : 'left',
		fields : [ 'compounds', 'activities' ],
		label : {
			rotate : {
				degrees : 315
			},

		},

	}, {
		title : 'Filter',
		type : 'Category',
		position : 'bottom',
		itemId : 'axis',
		label : {
			rotate : {
				degrees : 315
			},

		},
		fields : [ 'filter' ]
	} ],
	series : [ {
		type : 'line',
		highlight : {
			size : 7,
			radius : 7
		},
		axis : 'left',
		xField : 'filter',
		yField : 'compounds',
		markerConfig : {
			type : 'cross',
			size : 4,
			radius : 4,
			'stroke-width' : 0
		}

	}, {
		type : 'line',
		highlight : {
			size : 7,
			radius : 7
		},
		axis : 'left',
		xField : 'filter',
		yField : 'activities',
		markerConfig : {
			type : 'cross',
			size : 4,
			radius : 4,
			'stroke-width' : 0
		}

	} ]
});
