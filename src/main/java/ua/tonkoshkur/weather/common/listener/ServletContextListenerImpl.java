package ua.tonkoshkur.weather.common.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ua.tonkoshkur.weather.api.WeatherApi;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.factory.ComponentFactory;
import ua.tonkoshkur.weather.common.factory.ThymeleafFactory;
import ua.tonkoshkur.weather.common.util.AppProperties;
import ua.tonkoshkur.weather.session.ExpiredSessionCleanupScheduler;
import ua.tonkoshkur.weather.session.SessionDao;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener {

    private ExpiredSessionCleanupScheduler expiredSessionCleanupScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AppProperties appProperties = new AppProperties();
        ServletContext context = sce.getServletContext();
        initThymeleaf(context);
        initComponents(context, appProperties);
        startExpiredSessionCleanupScheduler(context);
    }

    private void initThymeleaf(ServletContext context) {
        ThymeleafFactory factory = new ThymeleafFactory(context);
        context.setAttribute(TemplateEngine.class.getSimpleName(), factory.getTemplateEngine());
        context.setAttribute(JakartaServletWebApplication.class.getSimpleName(), factory.getApplication());
    }

    private void initComponents(ServletContext context, AppProperties appProperties) {
        ComponentFactory factory = new ComponentFactory(appProperties);
        context.setAttribute(AuthService.class.getSimpleName(), factory.getAuthService());
        context.setAttribute(SessionDao.class.getSimpleName(), factory.getSessionDao());
        context.setAttribute(ExpiredSessionCleanupScheduler.class.getSimpleName(), factory.getExpiredSessionCleanupScheduler());
        context.setAttribute(WeatherApi.class.getSimpleName(), factory.getWeatherApi());
    }

    private void startExpiredSessionCleanupScheduler(ServletContext context) {
        String schedulerAttributeName = ExpiredSessionCleanupScheduler.class.getSimpleName();
        expiredSessionCleanupScheduler = (ExpiredSessionCleanupScheduler) context.getAttribute(schedulerAttributeName);
        expiredSessionCleanupScheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        expiredSessionCleanupScheduler.stop();
    }
}
