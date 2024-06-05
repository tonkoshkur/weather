package ua.tonkoshkur.weather.common.util;

import lombok.Getter;
import ua.tonkoshkur.weather.common.exception.LoadAppPropertiesException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppProperties {

    private final long sessionTtlMinutes;
    private final long expiredSessionsCleanupMinutes;
    private final String openWeatherApiKey;

    public AppProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            Properties props = new Properties();
            props.load(inputStream);
            sessionTtlMinutes = Long.parseLong(props.getProperty("session.ttl.minutes"));
            expiredSessionsCleanupMinutes = Long.parseLong(props.getProperty("session.expired.cleanup.minutes"));
            openWeatherApiKey = props.getProperty("api.openweather.key");
        } catch (IOException | IllegalArgumentException e) {
            throw new LoadAppPropertiesException(e);
        }
    }
}
