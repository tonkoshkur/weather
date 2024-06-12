package ua.tonkoshkur.weather.api.openweather;

import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.api.WeatherHttpClient;

import java.util.List;

public class OpenWeatherApiClient implements WeatherApiClient {

    private final OpenWeatherApi openWeatherApi;
    private final WeatherMapper weatherMapper;

    public OpenWeatherApiClient(WeatherHttpClient weatherHttpClient, String apiKey) {
        openWeatherApi = new OpenWeatherApi(weatherHttpClient, apiKey);
        weatherMapper = new WeatherMapper(openWeatherApi.getIconUrlFormat());
    }

    @Override
    public List<WeatherDto> findAllByCity(String city) throws WeatherApiException {
        return openWeatherApi.findGeoByCity(city).stream()
                .map(geoResponse -> weatherMapper.map(openWeatherApi.findByCity(geoResponse.getName()), geoResponse))
                .distinct()
                .toList();
    }
}