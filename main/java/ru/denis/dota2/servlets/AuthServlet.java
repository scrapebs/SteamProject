package ru.denis.dota2.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.denis.dota2.beans.SteamOpenID;
import ru.denis.dota2.util.Util;

/**
 *
 * @author denis
 */
public class AuthServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = ((SteamOpenID)request.getSession().getAttribute("openid")).verify(request.getRequestURL().toString(), request.getParameterMap());
        String fullUrl = Util.getFullUrl(request, "/");
        if (user == null) {
            response.sendRedirect(fullUrl);
        }
        request.getSession(true).setAttribute("steamid", user);
        response.sendRedirect(fullUrl);
    }
}