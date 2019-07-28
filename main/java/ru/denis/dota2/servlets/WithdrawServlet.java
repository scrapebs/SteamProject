package ru.denis.dota2.servlets;

import com.github.koraktor.steamcondenser.community.dota2.Dota2Inventory;
import com.github.koraktor.steamcondenser.community.SteamId;
import com.github.koraktor.steamcondenser.community.WebApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.denis.dota2.beans.SteamOpenID;
import ru.denis.dota2.db.ConnectDB;
import ru.denis.dota2.db.Inventory;
import ru.denis.dota2.db.InventoryWithoutPrice;
import ru.denis.dota2.db.InventoryShort;


/**
 *
 * @author denis
 */
public class WithdrawServlet extends HttpServlet{
     public static ArrayList<Inventory>  getInventoryItems (String id)  {
        try{ 
            URL oracle = new URL("http://steamcommunity.com/inventory/76561198106184535/570/2?l=english&count=5000");
            BufferedReader in = new BufferedReader(
            new InputStreamReader(oracle.openStream()));

            String inputLine, inventoryString="";
            while ((inputLine = in.readLine()) != null)
                inventoryString+=inputLine;
            in.close();

            JSONObject obj = new JSONObject(inventoryString);
            JSONArray assets = obj.getJSONArray("assets"); 
            JSONArray descriptions = obj.getJSONArray("descriptions"); 

            ArrayList<InventoryShort> ids = new ArrayList<InventoryShort>() ;
            InventoryShort elementitem;
            
            ArrayList<InventoryWithoutPrice> ids_full = new ArrayList<InventoryWithoutPrice>() ;
            InventoryWithoutPrice elementitem_full;

            for (int i = 0; i< assets.length(); i++)
            {
                JSONObject item = assets.getJSONObject(i);
                String iditemString = item.getString("assetid");
                String classidString = item.getString("classid");

                elementitem = new  InventoryShort (iditemString, classidString);
                ids.add(elementitem); 
            }  
            
            for (int i = 0; i< descriptions.length(); i++)
            {
                  JSONObject description = descriptions.getJSONObject(i); 
                  String classidString = description.getString("classid");   
                  int tradableInt = description.getInt("tradable");
                  int marketableInt = description.getInt("marketable");
                  if(tradableInt == 1 && marketableInt == 1){
                      for (int j = 0; j< assets.length(); j++)     {
                          if(classidString == null ? ids.get(j).classid == null : classidString.equals(ids.get(j).classid)){
                              String nameString = description.getString("market_hash_name");  
                              String imageString = description.getString("icon_url");
                              
                              elementitem_full = new  InventoryWithoutPrice (ids.get(j).iditem, ids.get(j).classid, nameString, imageString);
                              ids_full.add(elementitem_full);
                              break;
                          }
                      }
                  }
            } 

            ArrayList<Inventory> resaultinventory ;
            resaultinventory= ConnectDB.LoadInventory(ids_full);    
            return resaultinventory;
            }
            catch(Exception ex){
                ArrayList<Inventory> empty = new ArrayList();
                return empty;
        }
     }

    
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                request.getSession().setAttribute("openid", new SteamOpenID());
                String id = (String) request.getSession(true).getAttribute("steamid");
                if (id != null) {
                    request.getRequestDispatcher("/withdraw.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/withdraw.jsp").forward(request, response);
                }
            } finally {  }
     }
}