/*
 * File: app/view/PersonContacts.js
 *
 * This file was generated by Sencha Architect version 2.2.2.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('Simplereg.view.PersonContacts', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.personcontacts',

    frame: true,
    itemId: 'contacts',
    title: 'Contacts',
    store: 'Contacts',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            itemId: 'create-contact',
                            iconCls: 'icon-add',
                            text: 'Add'
                        },
                        {
                            xtype: 'button',
                            itemId: 'delete-contact',
                            iconCls: 'icon-delete',
                            text: 'Remove'
                        },
                        {
                            xtype: 'button',
                            itemId: 'update-contact',
                            iconCls: 'icon-edit',
                            text: 'Modify'
                        }
                    ]
                },
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true,
                    store: 'Contacts'
                }
            ],
            columns: [
                {
                    xtype: 'numbercolumn',
                    hidden: true,
                    dataIndex: 'id',
                    text: 'Id',
                    flex: 1,
                    format: '0'
                },
                {
                    xtype: 'numbercolumn',
                    hidden: true,
                    dataIndex: 'version',
                    text: 'Version',
                    flex: 1,
                    format: '0'
                },
                {
                    xtype: 'numbercolumn',
                    hidden: true,
                    dataIndex: 'personId',
                    hideable: false,
                    text: 'Person Id',
                    flex: 1,
                    format: '0'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'ctype',
                    text: 'Type',
                    flex: 1
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'countryCode',
                    text: 'Country',
                    flex: 1
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'address',
                    text: 'Address',
                    flex: 3
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'phoneNumber',
                    text: 'Phone',
                    flex: 2
                }
            ]
        });

        me.callParent(arguments);
    }

});