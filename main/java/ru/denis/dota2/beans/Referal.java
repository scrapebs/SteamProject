package ru.denis.dota2.beans;
import java.util.Date;

/**
 *
 * @author denis
 */
public class Referal {
    private String invited;
    private String date;
    private float cash;
    private int type;
    
    public Referal(String invited, String date, float cash, int type){
        this.invited = invited;
        this.date = date;
        this.cash = cash;
        this.type = type;
    }
    
    public String getInvited() {
        return invited;
    }
    
    public String getDate() {
        return date;
    }
    
    public float getCash() {
        return cash;
    }
     
    public int getType() {
        return type;
    }
}
