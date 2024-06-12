package ua.tonkoshkur.weather.api;

import java.util.List;

public interface WeatherApiClient {
    List<WeatherDto> findAllByCity(String city) throws WeatherApiException;
}
