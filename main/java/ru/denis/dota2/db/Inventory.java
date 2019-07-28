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
public class Inventory {
    public String iditem;
    public String classid;
    public String nameitem;
    public String imageitem;
    public float pricetotake;
    public float pricetogive;

    public Inventory(String iditem, String classid, String nameitem, String imageitem, float pricetotake, float pricetogive) {
        this.iditem = iditem;
        this.classid = classid;
        this.nameitem = nameitem;
        this.imageitem = imageitem;
        this.pricetotake = pricetotake;
        this.pricetogive = pricetogive;
    }
}
