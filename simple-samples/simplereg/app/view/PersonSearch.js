/*
 * File: app/view/PersonSearch.js
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

Ext.define('Simplereg.view.PersonSearch', {
    extend: 'Ext.window.Window',

    requires: [
        'Simplereg.view.override.PersonSearch'
    ],

    id: 'person-search',
    width: 400,
    closeAction: 'hide',
    iconCls: 'icon-search',
    title: 'Find Person',
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
                    api: {
    submit: "this.up('window').submit"
},
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            items: [
                                {
                                    xtype: 'button',
                                    itemId: 'reset',
                                    iconCls: 'icon-reset',
                                    text: 'Reset'
                                },
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
                                    iconCls: 'icon-search',
                                    text: 'Find Person'
                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            fieldLabel: 'First Name',
                            name: 'firstName'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            fieldLabel: 'Last Name',
                            name: 'lastName'
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: {
                                align: 'stretch',
                                type: 'hbox'
                            },
                            fieldLabel: 'Date of Birth',
                            items: [
                                {
                                    xtype: 'datefield',
                                    flex: 1,
                                    fieldLabel: 'From',
                                    hideLabel: true,
                                    name: 'dateOfBirthFrom',
                                    altFormats: 'd.m.Y',
                                    format: 'd.m.Y'
                                },
                                {
                                    xtype: 'displayfield',
                                    margin: '0 5',
                                    hideLabel: true,
                                    value: '&ndash;'
                                },
                                {
                                    xtype: 'datefield',
                                    flex: 1,
                                    fieldLabel: 'To',
                                    hideLabel: true,
                                    name: 'dateOfBirthTo',
                                    altFormats: 'd.m.Y',
                                    format: 'd.m.Y'
                                }
                            ]
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            fieldLabel: 'SSN',
                            name: 'ssn'
                        },
                        {
                            xtype: 'combobox',
                            anchor: '100%',
                            fieldLabel: 'Gender',
                            name: 'gender',
                            editable: false,
                            displayField: 'name',
                            store: 'Genders',
                            valueField: 'value'
                        }
                    ]
                }
            ]
        });

        me.callParent(arguments);
    }

});