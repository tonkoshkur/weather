package ua.tonkoshkur.weather.common.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ua.tonkoshkur.weather.common.factory.ComponentFactory;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initAttributes(sce.getServletContext());
    }

    private void initAttributes(ServletContext context) {
        ComponentFactory factory = new ComponentFactory(context);
        context.setAttribute(TemplateEngine.class.getSimpleName(), factory.getTemplateEngine());
        context.setAttribute(JakartaServletWebApplication.class.getSimpleName(), factory.getApplication());
    }
}
