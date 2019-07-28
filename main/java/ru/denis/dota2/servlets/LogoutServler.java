package ru.denis.dota2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.denis.dota2.beans.SteamOpenID;
import static ru.denis.dota2.util.Util.getFullUrl;

/**
 *
 * @author denis
 */
public class LogoutServler extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getSession(true).removeAttribute("steamid");

        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        for (Cookie cookie : cookies) { 
            if("claimed_id".equals(cookie.getName())){
                cookie.setValue("");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                break;
            }
        }
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
