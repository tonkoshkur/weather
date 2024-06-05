package ua.tonkoshkur.weather.api;

import java.util.List;

public interface WeatherApi {
    List<WeatherDto> findAllByCity(String city) throws WeatherApiException;
}
