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
public class Statistic {
    public int players;
    public int matches;
    public float money;

    public Statistic(int players, int matches, float money) {
        this.players = players;
        this.matches = matches;
        this.money = money;
    }
}
