package ua.tonkoshkur.weather.common.factory;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContext;
import lombok.Getter;
import org.hibernate.cfg.Configuration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.location.Location;
import ua.tonkoshkur.weather.session.OldSessionCleanupScheduler;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;
import ua.tonkoshkur.weather.user.UserDao;

@Getter
public class ComponentFactory {

    private final JakartaServletWebApplication application;
    private final WebApplicationTemplateResolver templateResolver;
    private final ITemplateEngine templateEngine;
    private final EntityManagerFactory entityManagerFactory;
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final AuthService authService;
    private final OldSessionCleanupScheduler oldSessionCleanupScheduler;

    public ComponentFactory(ServletContext servletContext) {
        application = JakartaServletWebApplication.buildApplication(servletContext);
        templateResolver = buildTemplateResolver();
        templateEngine = buildTemplateEngine();
        entityManagerFactory = buildEntityManagerFactory();
        userDao = new UserDao(entityManagerFactory);
        sessionDao = new SessionDao(entityManagerFactory);
        authService = new AuthService(sessionDao, userDao);
        oldSessionCleanupScheduler = new OldSessionCleanupScheduler(sessionDao);
    }

    private WebApplicationTemplateResolver buildTemplateResolver() {
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/view/");
        resolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        resolver.setCacheTTLMs(3600000L);
        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        resolver.setCacheable(true);
        return resolver;
    }

    private ITemplateEngine buildTemplateEngine() {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    private EntityManagerFactory buildEntityManagerFactory() {
        return new Configuration()
                .addAnnotatedClass(Location.class)
                .addAnnotatedClass(Session.class)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }
}
