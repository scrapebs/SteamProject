/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.denis.dota2.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.denis.dota2.beans.SteamOpenID;
import ru.denis.dota2.util.Util;

/**
 *
 * @author denis
 */
public class TradeServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String redirect = ((SteamOpenID)request.getSession().getAttribute("openid")).login(Util.getFullUrl(request, "/auth"));
        if(redirect != null) response.sendRedirect(redirect);
        else response.sendError(403, "Go Away!");
    }
}
