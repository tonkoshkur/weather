package ua.tonkoshkur.weather.common.util;

import lombok.Getter;
import ua.tonkoshkur.weather.common.exception.LoadAppPropertiesException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppProperties {

    private final long sessionExpirationMinutes;
    private final long expiredSessionsCleanupMinutes;

    public AppProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            Properties props = new Properties();
            props.load(inputStream);
            sessionExpirationMinutes = Long.parseLong(props.getProperty("session.expiration.minutes"));
            expiredSessionsCleanupMinutes = Long.parseLong(props.getProperty("session.expired.cleanup.minutes"));
        } catch (IOException | IllegalArgumentException e) {
            throw new LoadAppPropertiesException(e);
        }
    }
}