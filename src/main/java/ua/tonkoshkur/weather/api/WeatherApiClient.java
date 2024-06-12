package ua.tonkoshkur.weather.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WeatherApiClient {
    List<WeatherDto> findAllByCity(String city) throws WeatherApiException;

    Optional<WeatherDto> findByCoordinates(BigDecimal latitude, BigDecimal longitude) throws WeatherApiException;
}
