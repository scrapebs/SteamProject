/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.denis.dota2.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import ru.denis.dota2.db.ConnectDB;

/**
 *
 * @author denis
 */
public class Util {
    public static String getFullUrl(HttpServletRequest request, String path){
        StringBuilder builder = new StringBuilder("dotarate.ru"); //убрать после отладки   SERVER

        builder.insert(0, "http://");
        builder.append(path);
        return builder.toString();
    } 
    
    public static SimpleDateFormat getDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static void startObserve(final String id, final long timeStamp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(120000); //спи по 10мин
                        // HashMap<String, Boolean> matchResult = ResultsOfMatch.query(Long.parseLong(id), timeStamp);
                        // if(matchResult != null){
                        // ConnectDB.movedToPlayed(id, timeStamp, matchResult.get("result"));
                        return;
                      //  }
                    } catch (Exception ex) {
                   // Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                   // return;
                    }
                }
            }
        }).start();
    }
    
    public static void startObserveAll() {
        //for(Map.Entry<String, Long> entry : ConnectDB.getAllRates().entrySet()){
       //     startObserve(entry.getKey(), entry.getValue());
      //  }
    }
}
