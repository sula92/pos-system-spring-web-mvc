package com.pos.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Kept as a placeholder to avoid duplicate servlet registration.
 * DispatcherServlet is configured via WEB-INF/web.xml.
 */
public final class DispatcherServletInitializer /*extends AbstractAnnotationConfigDispatcherServletInitializer*/ {
    private DispatcherServletInitializer() {
    }

    /*@Override
    protected String[] getServletMappings() {
        return new String[0];
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[0];
    }*/
}

/*current status: It's a disabled placeholder with a private constructor to prevent instantiation.

Why it exists: The project uses web.xml to register the DispatcherServlet. If both web.xml and DispatcherServletInitializer (extending AbstractAnnotationConfigDispatcherServletInitializer) were active, they would cause duplicate servlet registration errors.

Can you delete it?: Yes. Since your project uses web.xml for servlet configuration, this file serves no purpose. You can safely remove it to clean up the codebase.

Your active configuration:

web.xml → registers DispatcherServlet and loads WebMvcConfig.java
WebMvcConfig.java → configures Spring, Hibernate, and component scanning
Alternative approach: If you wanted to use DispatcherServletInitializer instead of web.xml, you would:

Delete web.xml
Make DispatcherServletInitializer extend AbstractAnnotationConfigDispatcherServletInitializer
Implement the required methods (getRootConfigClasses, getServletConfigClasses, getServletMappings)
But since you're already using web.xml, keep that approach and delete the unused Java initializer.*/

//......................................................................................................

/*
*
* For Traditional Spring MVC (Non-Boot)
You need one of these to register the DispatcherServlet with the servlet container:

web.xml - XML-based servlet registration (your current approach)
DispatcherServletInitializer - Java-based servlet registration
Spring Boot - Handles everything automatically (no web.xml, no initializer needed)
The com.pos.config Package
Not compulsory at all. This is just a naming convention. You could:

Put WebMvcConfig.java in any package (e.g., com.pos, com.pos.configuration, config)
Name the package anything you want
Have multiple config packages if needed
Spring only cares about:

The class being annotated with @Configuration
web.xml pointing to the correct class path
Spring Boot Alternative
If you used Spring Boot, you wouldn't need:

web.xml
DispatcherServletInitializer
Manual WebMvcConfig (mostly auto-configured)
Just @SpringBootApplication and it works.

Summary
web.xml OR initializer: Required for traditional Spring MVC (to register DispatcherServlet)
com.pos.config package: Completely optional - just organizational preference
Spring Boot: Eliminates need for both
Your current setup (web.xml + WebMvcConfig) is valid and standard for traditional Spring MVC.
* */
