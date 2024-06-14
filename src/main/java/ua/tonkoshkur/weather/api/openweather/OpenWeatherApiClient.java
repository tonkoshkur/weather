package ua.tonkoshkur.weather.api.openweather;

import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.api.WeatherHttpClient;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OpenWeatherApiClient implements WeatherApiClient {

    private final OpenWeatherApi openWeatherApi;
    private final WeatherMapper weatherMapper;

    public OpenWeatherApiClient(WeatherHttpClient weatherHttpClient, String apiKey) {
        this(new OpenWeatherApi(weatherHttpClient, apiKey));
    }

    public OpenWeatherApiClient(OpenWeatherApi openWeatherApi) {
        this.openWeatherApi = openWeatherApi;
        this.weatherMapper = new WeatherMapper(openWeatherApi.getIconUrlFormat());
    }

    @Override
    public List<WeatherDto> findAllByCity(String city) throws WeatherApiException {
        return openWeatherApi.findGeoByCity(city)
                .stream()
                .map(geoResponse -> weatherMapper.map(
                        openWeatherApi.findWeatherByCoordinates(geoResponse.getLat(), geoResponse.getLon()),
                        geoResponse))
                .distinct()
                .toList();
    }

    @Override
    public Optional<WeatherDto> findByCoordinates(BigDecimal latitude, BigDecimal longitude) throws WeatherApiException {
        WeatherResponse weatherResponse = openWeatherApi.findWeatherByCoordinates(latitude, longitude);
        return openWeatherApi.findGeoByCoordinates(latitude, longitude)
                .stream()
                .map(geoResponse -> weatherMapper.map(weatherResponse, geoResponse))
                .findAny();
    }
}