package ua.tonkoshkur.weather.common.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.factory.ComponentFactory;
import ua.tonkoshkur.weather.session.OldSessionCleanupScheduler;
import ua.tonkoshkur.weather.session.SessionDao;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener {

    private OldSessionCleanupScheduler oldSessionCleanupScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initAttributes(sce.getServletContext());
        startOldSessionCleanupScheduler(sce.getServletContext());
    }

    private void initAttributes(ServletContext context) {
        ComponentFactory factory = new ComponentFactory(context);
        context.setAttribute(TemplateEngine.class.getSimpleName(), factory.getTemplateEngine());
        context.setAttribute(JakartaServletWebApplication.class.getSimpleName(), factory.getApplication());
        context.setAttribute(AuthService.class.getSimpleName(), factory.getAuthService());
        context.setAttribute(SessionDao.class.getSimpleName(), factory.getSessionDao());
        context.setAttribute(OldSessionCleanupScheduler.class.getSimpleName(), factory.getOldSessionCleanupScheduler());
    }

    private void startOldSessionCleanupScheduler(ServletContext context) {
        String schedulerAttributeName = OldSessionCleanupScheduler.class.getSimpleName();
        oldSessionCleanupScheduler = (OldSessionCleanupScheduler) context.getAttribute(schedulerAttributeName);
        oldSessionCleanupScheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        oldSessionCleanupScheduler.stop();
    }
}
