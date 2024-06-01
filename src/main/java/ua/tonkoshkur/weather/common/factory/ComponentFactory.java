package ua.tonkoshkur.weather.common.factory;

import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import org.hibernate.cfg.Configuration;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.util.AppProperties;
import ua.tonkoshkur.weather.location.Location;
import ua.tonkoshkur.weather.session.ExpiredSessionCleanupScheduler;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;
import ua.tonkoshkur.weather.user.UserDao;

@Getter
public class ComponentFactory {

    private final EntityManagerFactory entityManagerFactory;
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final AuthService authService;
    private final ExpiredSessionCleanupScheduler expiredSessionCleanupScheduler;

    public ComponentFactory(AppProperties appProperties) {
        long sessionExpirationMinutes = appProperties.getSessionExpirationMinutes();
        long expiredSessionsCleanupMinutes = appProperties.getExpiredSessionsCleanupMinutes();

        entityManagerFactory = buildEntityManagerFactory();
        userDao = new UserDao(entityManagerFactory);
        sessionDao = new SessionDao(entityManagerFactory);
        authService = new AuthService(sessionExpirationMinutes, sessionDao, userDao);
        expiredSessionCleanupScheduler = new ExpiredSessionCleanupScheduler(expiredSessionsCleanupMinutes, sessionDao);
    }

    private EntityManagerFactory buildEntityManagerFactory() {
        return new Configuration()
                .addAnnotatedClass(Location.class)
                .addAnnotatedClass(Session.class)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }
}
