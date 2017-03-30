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

var rowEditing = Ext.create('Ext.grid.plugin.RowEditing');

Ext.define('AM.view.protocol.ProtocolGrid', {
	plugins : [ rowEditing, {
        ptype: 'gridviewdragdrop',
        dragGroup: 'firstGridDDGroup',
        dropGroup: 'firstGridDDGroup'
    } ],
	extend : 'Ext.grid.Panel',
	alias : 'widget.protocolgrid',
	store : 'Protocol',
	itemId : 'protocolgrid',
	selType : 'rowmodel',
	autoScroll : true,
	closable: true,
	closeAction: 'hide',
	layout:'fit',
	heigth: 300,
	initComponent : function() {
		this.columns = [ 
		{
			header : 'Filter Step',
			dataIndex : 'filter_description',
			flex : 1,
			editor : {
				xtype : 'combobox',
				store : Ext.create('AM.store.Filter'),
				queryMode : 'remote',
				queryParam : 'filter_string',
				displayField : 'filter_description',
				valueField : 'filter_description',
				//forceSelection : true,
				allowBlank : true,
				typeAhead : true,
				typeAheadDelay : 100,
				queryDelay : 100,
				queryCaching : false,
				width : 600,
				matchFieldWidth : true,
				minChars : 2,
				hideTrigger : true,
				emptyText : 'Start typing...',
				fieldLabel : 'Filter description',
			}
		}
		
		];
		this.callParent(arguments);
	},
	dockedItems : [ {
		xtype : 'toolbar',
		items : [
				{
					text : 'Add filter',
					iconCls : 'icon-add',
					handler : function() {
						console.log("Protocol Add!")
						var protocolstore = Ext.data.StoreManager.lookup('Protocol');
						protocolstore.insert(0, new AM.model.Protocol());
						rowEditing.startEdit(0, 0);
				}
				},
				'-',
				{
					text : 'Save protocol',
					// iconCls : 'icon-delete',
					handler : function() {
							console.log("Protocol Save!")
							var protocolstore = Ext.data.StoreManager.lookup('Protocol');
							protocolstore.sync();
							var protocolgrid = Ext.ComponentQuery.query('#protocolgrid');
							console.log(protocolgrid[0]);
							protocolgrid[0].hide();		
						
					}
				},
				'-',
				{
					text : 'Cancel',
					// iconCls : 'icon-delete',
					handler : function() {
						var protocolgrid = Ext.ComponentQuery.query('#protocolgrid');
						console.log(protocolgrid[0]);
						protocolgrid[0].hide();						
					}
				}
				]
	} ],
	
	
});