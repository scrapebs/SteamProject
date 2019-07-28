package ru.denis.dota2.servlets;

import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.apache.commons.httpclient.HttpClient;
import com.github.koraktor.steamcondenser.community.dota2.Dota2Inventory;
import com.github.koraktor.steamcondenser.community.GameInventory;
import com.github.koraktor.steamcondenser.community.SteamId;
import com.github.koraktor.steamcondenser.community.WebApi;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ru.denis.dota2.beans.SteamOpenID;
import com.github.koraktor.steamcondenser.servers.SteamPlayer;
import com.github.koraktor.steamcondenser.servers.GameServer;
import com.github.koraktor.steamcondenser.servers.SourceServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import ru.denis.dota2.db.ConnectDB;
import ru.denis.dota2.db.Inventory;
import ru.denis.dota2.db.InventoryShort;
import ru.denis.dota2.db.InventoryWithoutPrice;


/**
 *
 * @author denis
 */
public class InventoryServlet extends HttpServlet{
    public static ArrayList<Inventory>  getInventoryItems (String id)  {
        try{ 
            URL oracle = new URL("http://steamcommunity.com/inventory/"+id+"/570/2?l=english&count=5000");
            BufferedReader in = new BufferedReader(
            new InputStreamReader(oracle.openStream()));

            String inputLine, inventoryString="";
            while ((inputLine = in.readLine()) != null)
                inventoryString+=inputLine;
            in.close();

            JSONObject obj = new JSONObject(inventoryString);
            JSONArray assets = obj.getJSONArray("assets"); 
            JSONArray descriptions = obj.getJSONArray("descriptions"); 

            ArrayList<InventoryShort> ids = new ArrayList<InventoryShort>();
            InventoryShort elementitem;
            
            ArrayList<InventoryWithoutPrice> ids_full = new ArrayList<InventoryWithoutPrice>();
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
                request.getRequestDispatcher("/inventory.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } finally {  }
    }
}

 