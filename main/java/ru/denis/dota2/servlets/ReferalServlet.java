/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class ReferalServlet extends HttpServlet{
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            request.getSession().setAttribute("openid", new SteamOpenID());
            String id = (String) request.getSession(true).getAttribute("steamid");
            if (id != null) {
               
                request.getRequestDispatcher("/referal.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } finally {  }
    }
}

 