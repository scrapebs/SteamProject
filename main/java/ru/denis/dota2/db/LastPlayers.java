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
public class lastPlayers {
    public String iduser;
    public float rate;

    public lastPlayers(String iduser, float rate) {
        this.iduser = iduser;
        this.rate = rate;

    }
}
