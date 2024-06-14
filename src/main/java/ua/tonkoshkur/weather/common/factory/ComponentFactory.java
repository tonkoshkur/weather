package ua.tonkoshkur.weather.common.factory;

import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import org.hibernate.cfg.Configuration;
import org.modelmapper.ModelMapper;
import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherHttpClient;
import ua.tonkoshkur.weather.api.openweather.OpenWeatherApiClient;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.properties.AppProperties;
import ua.tonkoshkur.weather.location.Location;
import ua.tonkoshkur.weather.location.LocationDao;
import ua.tonkoshkur.weather.location.LocationMapper;
import ua.tonkoshkur.weather.location.weather.LocationWeatherRepository;
import ua.tonkoshkur.weather.location.weather.LocationWeatherService;
import ua.tonkoshkur.weather.session.ExpiredSessionCleanupScheduler;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;
import ua.tonkoshkur.weather.user.UserDao;

@Getter
public class ComponentFactory {

    private final EntityManagerFactory entityManagerFactory;
    private final WeatherApiClient weatherApiClient;
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final LocationDao locationDao;
    private final LocationWeatherRepository locationWeatherRepository;
    private final LocationWeatherService locationWeatherService;
    private final AuthService authService;
    private final ExpiredSessionCleanupScheduler expiredSessionCleanupScheduler;

    public ComponentFactory(AppProperties appProperties) {
        long sessionTtlMinutes = appProperties.getSessionTtlMinutes();
        long expiredSessionsCleanupMinutes = appProperties.getExpiredSessionsCleanupMinutes();

        entityManagerFactory = buildEntityManagerFactory();
        weatherApiClient = new OpenWeatherApiClient(new WeatherHttpClient(), appProperties.getOpenWeatherApiKey());
        userDao = new UserDao(entityManagerFactory);
        sessionDao = new SessionDao(entityManagerFactory);
        locationDao = new LocationDao(entityManagerFactory);
        ModelMapper modelMapper = new ModelMapper();
        LocationMapper locationMapper = new LocationMapper(modelMapper);
        locationWeatherRepository = new LocationWeatherRepository(locationDao, weatherApiClient, locationMapper);
        locationWeatherService = new LocationWeatherService(locationWeatherRepository);
        authService = new AuthService(sessionTtlMinutes, sessionDao, userDao);
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
