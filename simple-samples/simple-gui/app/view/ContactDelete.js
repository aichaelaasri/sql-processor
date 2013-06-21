/*
 * File: app/view/ContactDelete.js
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

Ext.define('Simplereg.view.ContactDelete', {
    extend: 'Ext.window.Window',

    requires: [
        'Simplereg.view.override.ContactDelete'
    ],

    id: 'contact-delete',
    itemId: 'dialog',
    width: 400,
    closeAction: 'hide',
    iconCls: 'icon-delete',
    title: 'Remove Contact',
    modal: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'form',
                    bodyPadding: 10,
                    header: false,
                    title: 'Data',
                    trackResetOnLoad: true,
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            items: [
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    itemId: 'cancel',
                                    iconCls: 'icon-cancel',
                                    text: 'Cancel'
                                },
                                {
                                    xtype: 'button',
                                    itemId: 'submit',
                                    iconCls: 'icon-delete',
                                    text: 'Remove Contact'
                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'numberfield',
                            anchor: '100%',
                            hidden: true,
                            fieldLabel: 'Person Id',
                            name: 'id',
                            readOnly: true
                        },
                        {
                            xtype: 'numberfield',
                            anchor: '100%',
                            hidden: true,
                            fieldLabel: 'Person Id',
                            name: 'version',
                            readOnly: true
                        },
                        {
                            xtype: 'numberfield',
                            anchor: '100%',
                            hidden: true,
                            fieldLabel: 'Person Id',
                            name: 'personId',
                            readOnly: true
                        },
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            fieldLabel: 'Type',
                            name: 'ctype'
                        },
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            fieldLabel: 'Country',
                            name: 'countryCode'
                        },
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            fieldLabel: 'Address',
                            name: 'address'
                        },
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            fieldLabel: 'Phone',
                            name: 'phoneNumber'
                        }
                    ]
                }
            ]
        });

        me.callParent(arguments);
    }

});