package ru.denis.dota2.beans;

import java.util.Date;
import ru.denis.dota2.util.Util;

/**
 *
 * @author denis
 */
public class GameInfo {
    private boolean win;
    private String date;
    private float count;

    public GameInfo(boolean win, Date date, float count) {
        this.win = win;
        this.date = Util.getDateFormat().format(date);
        this.count = count;
    }

    public boolean isWin() {
        return win;
    }

    public String getDate() {
        return date;
    }

    public float getCount() {
        return count;
    }
}
