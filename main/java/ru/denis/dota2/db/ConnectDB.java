package ru.denis.dota2.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import ru.denis.dota2.beans.GameInfo;
import ru.denis.dota2.beans.Referal;
import ru.denis.dota2.util.SteamGetFuncs;
import ru.denis.dota2.util.Util;
import java.util.ArrayList;
import org.json.JSONObject;
import ru.denis.dota2.db.Inventory;
import ru.denis.dota2.db.InventoryShort;
import ru.denis.dota2.db.InventoryWithoutPrice;
import ru.denis.dota2.db.lastPlayers;

/**
 *
 * @author denis
 */
public class ConnectDB {
    
    private static final float COEFFICIENT = (float) 1.3;
    private static final float initial_cash = 10;
    private static float inviting_bonus = 0.5F;
    private static float invited_bonus = 1;
    
    private static SimpleDateFormat dateFormat = Util.getDateFormat();
    
    static{
        Locale.setDefault(Locale.ROOT);
    }
    // Проверка, есть ли на данный момент активная ставка
    public static HashMap<String, Object> isRate(String id){
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("isRate", false);
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM rates WHERE id = %s", id));
            if(rs.next()){
                result.put("isRate", true);
                result.put("rate", rs.getFloat("rate"));
                result.put("date", new Date(rs.getTimestamp("date").getTime()));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    // Получаем количество средств на счету авторизованного пользователя + инициализация нового счета пользователя
    public static float getCount(String id){
        float cash = 0;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = %s", id));
            if(rs.next())
                cash = rs.getFloat("cash");
            else{
                cash = initial_cash;
                stmt.executeUpdate(String.format("INSERT INTO accounts (id, cash, token) VALUES('%s', %f, '')", id, cash));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return cash;
    }


    // Получаем количество средств на счету авторизованного пользователя + инициализация нового счета реферала
    // Добавление средств на счет пригласителя при регестрации реферала
    public static float referal_getCount(String invited, String inviting){
        float cash = 0;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = %s", invited));
            if(rs.next())
                cash = rs.getFloat("cash");
            else{
                rs2 = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = %s", inviting));
                if(rs2.next()){
                    cash = invited_bonus;
                    stmt.executeUpdate(String.format("INSERT INTO accounts (id, cash, token) VALUES('%s', %f, '')", invited, cash));
                    stmt.executeUpdate(String.format("UPDATE accounts SET cash = cash + %f WHERE id = %s", inviting_bonus, inviting));
                    stmt.executeUpdate(String.format("INSERT INTO referals (inviting, invited, date, cash, type) VALUES('%s','%s', '%s', %f, %d)", inviting, invited,dateFormat.format(new Date()), inviting_bonus, 1 ));
                }
                else{
                    cash = initial_cash;
                    stmt.executeUpdate(String.format("INSERT INTO accounts (id, cash, token) VALUES('%s', %а, '')", invited, cash));
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                if(cash == invited_bonus)
                    rs2.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return cash;
    }
    

    // Произвести ставку
    public static String bet(String id, float rate) {
        String result = null;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = %s", id));
            rs.next();
            if(rate<1)
                result = "Ставка должна быть больше либо равна 1";
            else
                if(rs.getFloat("cash") < rate)
                result = "Недостаточно денег на счету";
            else{
                long timeStamp = SteamGetFuncs.getTimeStamp();
                result = "Ставка принята!";
                Util.startObserve(id, timeStamp);
                stmt.executeUpdate(String.format("UPDATE accounts SET cash = cash - %f WHERE id = %s", rate, id));
                stmt.executeUpdate(String.format("INSERT INTO rates (id, rate, date, timestamp) VALUES('%s', '%f', '%s', %s)", id, rate, dateFormat.format(new Date()), timeStamp));
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    // Проверка, является ли история матчей по Dota2 публичной для данного пользователя
    // В случае, если история закрыта, пользователю будет отказано в произведение ставок
    public static boolean isHistoryOpen(String id) {
        boolean result = false;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = %s", id));
            rs.next();
            if(rs.getInt("openhistory") == 1)
                result = true;
  
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    // Запомнить, что история матчей пользователя открыта
    public static void makeHistoryOpen (String id){
        boolean result = false;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            stmt.executeUpdate(String.format("UPDATE accounts SET openhistory = 1  WHERE id = %s", id));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    // Запомнить, что история матчей пользователя закрыта
    public static void makeHistoryClose (String id){
        boolean result = false;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            stmt.executeUpdate(String.format("UPDATE accounts SET openhistory = 0  WHERE id = %s", id));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    // Получаем историю последних 10 ставок пользователя
    public static ArrayList<GameInfo> getTop10Game(String id){
        ArrayList<GameInfo> result = new ArrayList<GameInfo>();
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM history WHERE id = %s ORDER BY timestamp Desc limit 10", id));
            while(rs.next()){
                result.add(new GameInfo(rs.getFloat("rate") > 0, new Date(rs.getTimestamp("date").getTime()), Math.abs(rs.getFloat("rate"))));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    // Получаем список 20 последних приглашенных рефералов
    public static ArrayList<Referal> getTop20Referals(String id){
        ArrayList<Referal> result = new ArrayList<Referal>();
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM referals WHERE inviting = %s ORDER BY date Desc limit 20", id));
            while(rs.next()){
                result.add(new Referal(rs.getString("invited"), rs.getString("date"), rs.getFloat("cash"), rs.getInt("type")));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    public static void movedToPlayed(String id, long timeStamp, boolean win){
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM rates WHERE id = %s", id));
            rs.next();
            float iSign = win ? COEFFICIENT : -1F;
            float rate = rs.getFloat("rate");
            stmt.executeUpdate(String.format("INSERT INTO history (id, rate, date, timestamp) VALUES('%s', '%f', '%s', %s)", id, rate * iSign, dateFormat.format(new Date(rs.getTimestamp("date").getTime())), timeStamp));
            if(win)
                stmt.executeUpdate(String.format("UPDATE accounts SET cash = cash + %f WHERE id = %s", rate * COEFFICIENT, id));
            else{
               // rs2 = stmt.executeQuery(String.format("SELECT * FROM referals WHERE invited = %s", id));
               // if(rs2.next()){
               //     stmt.executeUpdate(String.format("UPDATE accounts SET cash = cash + %f WHERE id = %s", rate * 0.05, rs2.getString("inviting")));
               //     stmt.executeUpdate(String.format("INSERT INTO referals (inviting, invited, date, cash, type) VALUES('%s','%s', '%s', %f, %d)", rs2.getString("inviting"), id,dateFormat.format(new Date()), rate*0.05, 2 ));
               // }
            }
            
            stmt.executeUpdate(String.format("DELETE FROM rates WHERE id = %s", id));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                if(!win)
                    rs2.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public static HashMap<String, Long> getAllRates(){
        HashMap<String, Long> result = new HashMap<String, Long>();
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM rates"));
            while(rs.next()){
                result.put(rs.getString("id"), rs.getLong("timestamp"));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    // Проверяем, есть ли у пользователя ставки
    public static Long getPlayerRate(String id){
        Long result = 0L;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM rates where id = %s", id));
            while(rs.next()){
                result = rs.getLong("timestamp");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
              
            }
        }
        return result;
    }


    //for index page to show last players
    public static ArrayList<lastPlayers> lastPlayers(){   //String id
        int count = 0;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        lastPlayers player;
        ArrayList<lastPlayers> players = new ArrayList<lastPlayers>();
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT id, rate FROM history ORDER BY timestamp Desc limit 5")); 
           // items 
            while(rs.next())
            {
                player = new  lastPlayers (rs.getString("id"), rs.getFloat("rate"));
                players.add(player);
                count++;
            }
            return players;
        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return players;
    }

    
    //return cash of players, games and withdraw money
    public static Statistic getStatistic(){   //String id
        int count = 0;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        Statistic stat;
        int players, matches;
        float money;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT count(*) FROM accounts")); 
            rs.next();
            players = rs.getInt(1);
            rs2 = stmt.executeQuery(String.format("SELECT count(*) FROM history")); 
            rs2.next();
            matches =rs2.getInt(1);
            rs3 = stmt.executeQuery(String.format("SELECT sum(rate) FROM history where rate>0")); 
            rs3.next();
            money =rs3.getFloat(1);
            stat = new  Statistic (players, matches, money);
            return stat;
        }
        catch(Exception ex){
            ex.printStackTrace(); 
            stat = new  Statistic (788, 2865, 3456);
        }finally{
            try {
                rs.close();
                rs2.close();
                rs3.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      return stat;   
    }
    
    
    // For withdraw
    // Получаем предметы, доступные для вывода из инвентаря бота
    public static ArrayList<Inventory> CountItems(){
        int count = 0;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        Inventory elementitem;
        ArrayList<Inventory> arritems = new ArrayList<Inventory>();
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT botinventory.iditem, shmot.classid, shmot.nameitem, imageitem, pricetogive  FROM botinventory, shmot WHERE botinventory.classid = shmot.classid")); 
           // items 
            while(rs.next())
            {
                elementitem = new  Inventory (rs.getString("iditem"), rs.getString("classid"), rs.getString("nameitem"), rs.getString("imageitem"), 0, rs.getFloat("pricetogive"));
                arritems.add(elementitem);
                count++;
            }
            return arritems;
        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      return arritems;   
    }


    public static boolean isWithdrawQuery(String id){
        boolean isWithdrawQuery = false; 
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM reqwithdraw WHERE iduser = %s", id));
            if(rs.next()){
                isWithdrawQuery = true; 
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return isWithdrawQuery;
    }
     
   public static String CreateTradeLink(String id, String tradelink){   //
       int count = 0;
        String result = "";
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
       
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            String check[] = tradelink.split("=");
          //  if(check[0]=="https://steamcommunity.com/tradeoffer/new/?partner")  {
                stmt.executeUpdate(String.format("update accounts SET token ='%s' where id ='%s'", tradelink, id));
                         result = "add token";
                      
          //  }
            
        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                
               
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return result;
    }


    public static String getTradeLink(String id){
       int count = 0;
        String result = "";
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        String tradelink="";
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = '%s'", id));
            if(rs.next()){
                tradelink = rs.getString("token"); 
            }
        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return tradelink;
    }
    

    public static String CreateQueryDepozit(String id, String select[]){   //
        int count = 0;
        String result = "";
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
       
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            String  iditem="";
            String [] parts;
            parts=select[0].split("/");
            float priceitem=Float.parseFloat(parts[0]);
            iditem = parts[1];
            for (int i = 1; i < select.length; i++) { 
                parts =select[i].split("/");
                iditem +="/"+ parts[1];
                priceitem += Float.parseFloat(parts[0]);
            }
                {
                     long timeStamp = SteamGetFuncs.getTimeStamp();
                     stmt.executeUpdate(String.format("insert into reqdepozit (iduser, iditem, priceitem, status, timestamp) values ('%s', '%s', '%f', 'active', '%s')", id, iditem, priceitem, timeStamp));
                     result = "query created";
                }

        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                
               // rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return result;
    }


    public static String CreateQueryWithdraw(String id, String select[]){   //
        int count = 0;
        String result = "";
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
       
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM accounts WHERE id = %s", id));
            rs.next();
            //String priceitem="";
            String  iditem="";
            String [] parts;
            parts=select[0].split("/");
          //  priceitem = parts[0];
            iditem = parts[1];
            float priceitem=Float.parseFloat(parts[0]);
            for (int i = 1; i < select.length; i++) { 
                parts =select[i].split("/");
             //   priceitem +="/"+ parts[0];
                iditem +="/"+ parts[1];
                priceitem += Float.parseFloat(parts[0]);
            }
                {
                    if(rs.getFloat("cash") > priceitem) 
                    {
                        long timeStamp = SteamGetFuncs.getTimeStamp();
                        stmt.executeUpdate(String.format("insert into reqwithdraw (iduser, iditem, priceitem, status, timestamp) values ('%s', '%s', '%f', 'active', '%s')", id, iditem, priceitem, timeStamp));
                        result = "query created";
                        stmt.executeUpdate(String.format("UPDATE accounts SET cash = cash - %f WHERE id = %s", priceitem, id));
                        
                    }
                    else result = "not enough money";
                }
        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    public static boolean isDepozitQuery(String id){
        boolean isWithdrawQuery = false; 
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            rs = stmt.executeQuery(String.format("SELECT * FROM reqdepozit WHERE iduser = %s", id));
            if(rs.next()){
                isWithdrawQuery = true; 
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try {
                stmt.close();
                rs.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return isWithdrawQuery;
    }


    //принимает спиок айдишников предметов, возвращает json массив из айди, изображения, имени и цены
    public static ArrayList <Inventory> LoadInventory(ArrayList<InventoryWithoutPrice> ids){   //String id
        int count = 0;
        InitialContext ic;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;

        Inventory elementitem;
        ArrayList<Inventory> arritems = new ArrayList<Inventory>();

        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:jboss/datasources/MySQLDS");
            conn = ds.getConnection();
            stmt = conn.createStatement(); 
            String idin="\""+ids.get(0).nameitem+"\"";
            for (int i = 1; i < ids.size(); i++) {
               idin += ", \""+  ids.get(i).nameitem + "\" ";
            }
            rs = stmt.executeQuery(String.format("SELECT * FROM shmot_price WHERE nameitem in (%s) AND priceitem > 0", idin));  
           // items 
            while(rs.next())
            {
                count++; 
                for(int i=0;i<ids.size();i++){
                    if(rs.getString("nameitem").equals(ids.get(i).nameitem)){
                        float pricetotake = rs.getFloat("priceitem");
                        pricetotake *= 0.6;
                        pricetotake = new BigDecimal(pricetotake).setScale(2, RoundingMode.UP).floatValue(); 
                        float pricetogive = rs.getFloat("priceitem");
                        pricetogive *= 1.3;
                        pricetogive = new BigDecimal(pricetogive).setScale(2, RoundingMode.UP).floatValue();
                        elementitem = new  Inventory (ids.get(i).iditem, ids.get(i).classid, ids.get(i).nameitem, ids.get(i).imageitem, pricetotake, pricetogive);
                        arritems.add(elementitem);
                        break;
                   }
                }
            }
            return arritems;
        }
        catch(Exception ex){
            ex.printStackTrace(); 
        }finally{
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      return arritems;   
    }
}