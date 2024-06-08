package ua.tonkoshkur.weather.api;

public class WeatherApiException extends RuntimeException {
    public WeatherApiException(String message) {
        super(message);
    }

    public WeatherApiException(Throwable cause) {
        super(cause);
    }
}
