package com.pos.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Hibernate/JPA bootstraps schema via Spring config; no manual JDBC call needed.
        System.out.println("[POS-Spring] Application starting up...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[POS-Spring] Application shutting down...");
    }
}
