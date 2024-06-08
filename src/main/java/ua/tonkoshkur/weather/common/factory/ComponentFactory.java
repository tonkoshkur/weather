package ua.tonkoshkur.weather.common.factory;

import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import org.hibernate.cfg.Configuration;
import ua.tonkoshkur.weather.api.WeatherApi;
import ua.tonkoshkur.weather.api.WeatherHttpClient;
import ua.tonkoshkur.weather.api.openweather.OpenWeatherApi;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.properties.AppProperties;
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
    private final WeatherApi weatherApi;
    private final ExpiredSessionCleanupScheduler expiredSessionCleanupScheduler;

    public ComponentFactory(AppProperties appProperties) {
        long sessionTtlMinutes = appProperties.getSessionTtlMinutes();
        long expiredSessionsCleanupMinutes = appProperties.getExpiredSessionsCleanupMinutes();

        entityManagerFactory = buildEntityManagerFactory();
        userDao = new UserDao(entityManagerFactory);
        sessionDao = new SessionDao(entityManagerFactory);
        authService = new AuthService(sessionTtlMinutes, sessionDao, userDao);
        weatherApi = new OpenWeatherApi(new WeatherHttpClient(), appProperties.getOpenWeatherApiKey());
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
