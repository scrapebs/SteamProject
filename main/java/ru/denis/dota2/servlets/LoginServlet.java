package ru.denis.dota2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.*;
import ru.denis.dota2.beans.SteamOpenID;
import ru.denis.dota2.util.Util;


/**
 *
 * @author denis
 */
public class LoginServlet extends HttpServlet {
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            request.getSession().setAttribute("openid", new SteamOpenID());
            String id = (String) request.getSession(true).getAttribute("steamid");
            if (id != null) {
                 boolean cook = false;
                 Cookie[] cookies = request.getCookies();
                 for(Cookie cookie : cookies){
                    if("claimed_id".equals(cookie.getName())){
                        cook = true;
                    }
                 }
                 if(cook == false){
                    Cookie cookie = new Cookie("claimed_id", id);
                    cookie.setMaxAge(365 * 24 * 60 * 60); // 24 hours.
                    response.addCookie(cookie); 
                 }
                 request.getRequestDispatcher("main.jsp").forward(request, response);
            } else {
                    boolean cook = false;
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null)
                    for(Cookie cookie : cookies){
                        if("claimed_id".equals(cookie.getName())){
                            id = cookie.getValue();
                            request.getSession(true).setAttribute("steamid", id);
                            cook = true;
                            break;
                        }
                    }
                    if(cook)
                        request.getRequestDispatcher("main.jsp").forward(request, response);
                    else
                        request.getRequestDispatcher("index.jsp").forward(request, response);
            }
    }
}
