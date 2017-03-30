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
 
Ext.define('AM.view.job.Grid' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.jobgrid',
    store: 'Job',
    itemId: 'jobgrid',
    selType: 'rowmodel', 
    autoScroll: true,
    columns : [
               {header: 'Id',  dataIndex: 'job_id',  flex: 1,hidden:true},
               {header: 'Title', dataIndex: 'job_description', flex: 1},
               {header: 'Target', dataIndex: 'job_extraction_description', flex: 1},
               {header: 'Filtering protocol', dataIndex: 'job_filtering_description', flex: 1}
           ]
});