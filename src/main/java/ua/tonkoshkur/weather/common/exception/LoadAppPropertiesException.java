package ua.tonkoshkur.weather.common.exception;

public class LoadAppPropertiesException extends RuntimeException {
    public LoadAppPropertiesException(Throwable cause) {
        super("Unable to load application properties", cause);
    }
}
