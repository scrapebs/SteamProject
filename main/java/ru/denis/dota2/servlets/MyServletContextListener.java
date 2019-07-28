/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.denis.dota2.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import ru.denis.dota2.util.Util;

/**
 * Web application lifecycle listener.
 *
 * @author denis
 */
public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Util.startObserveAll();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
