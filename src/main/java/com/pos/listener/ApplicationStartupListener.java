package com.pos.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

//@WebListener - This annotation is used to register the listener with the servlet container
//Without this annotation, the listener will not be registered with the servlet container.
//A listener is a component that is notified of events that occur in the servlet container (Events such as application startup and shutdown).
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

/*
* You don't actually need ApplicationStartupListener for your current setup.

What It Currently Does
Looking at the code, it only:

Prints [POS-Spring] Application starting up... when the app starts
Prints [POS-Spring] Application shutting down... when the app stops
Why It Exists (Historical Context)
The comment suggests this was used in a JDBC version for manual database initialization. Now that you're using Hibernate/JPA with Spring:

Hibernate auto-creates tables via hibernate.ddl-auto=create-drop in WebMvcConfig
No manual JDBC initialization needed
This listener is just logging startup/shutdown events
Can You Delete It?
Yes. You can safely delete it because:

Hibernate handles schema initialization automatically
Spring's @PostConstruct in WebMvcConfig already logs startup
It provides no essential functionality
When Would You Need It?
You'd keep it if you wanted to:

Run custom initialization logic on startup (load reference data, cache warming, etc.)
Log specific startup/shutdown events
Initialize resources that need servlet context access
For your current POS system, it's redundant and can be removed.
* */