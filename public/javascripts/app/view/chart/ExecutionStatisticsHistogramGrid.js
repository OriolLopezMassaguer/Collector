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

Ext.define('AM.view.chart.ExecutionStatisticsHistogramGrid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.statisticshistogramgrid', 
    store: 'ExecutionStatisticsHistogram',
    autoScroll: true,
    width: 1100,
    height: 500,
    padding : '50 50 50 50',
    initComponent: function() {
    	console.log("histogram view init!!!");
        this.columns = [
                        {header: 'job_execution_id',  dataIndex: 'job_execution_id',  flex: 1},		
			            {header: 'activities',  dataIndex: 'activities',  flex: 1},
			            {header: 'bucket', dataIndex: 'bucket', flex: 1},
			            {header: 'descriptor', dataIndex: 'descriptor', flex: 1}
        ];

        this.callParent(arguments);
    }

});


