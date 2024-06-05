package ua.tonkoshkur.weather.api;

public class WeatherApiException extends RuntimeException {

    private static final String MESSAGE = "Unable to find weather";

    public WeatherApiException() {
        super(MESSAGE);
    }

    public WeatherApiException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
