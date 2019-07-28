/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.denis.dota2.db;

/**
 *
 * @author denis
 */
public class InventoryShort {
    public String iditem;
    public String classid;

    public InventoryShort(String iditem, String classid) {
        this.iditem = iditem;
        this.classid = classid;
    }
}