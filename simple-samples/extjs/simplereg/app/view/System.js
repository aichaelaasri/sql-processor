/*
 * File: app/view/System.js
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

Ext.define('Simplereg.view.System', {
    extend: 'Ext.button.Button',
    alias: 'widget.system',

    text: 'System',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            menu: {
                xtype: 'menu',
                items: [
                    {
                        xtype: 'menuitem',
                        action: 'simpleauth',
                        hrefTarget: 'simpleauth',
                        text: 'Users and Roles'
                    },
                    {
                        xtype: 'menuseparator'
                    },
                    {
                        xtype: 'menuitem',
                        action: 'quit',
                        text: 'Quit'
                    }
                ]
            }
        });

        me.callParent(arguments);
    }

});