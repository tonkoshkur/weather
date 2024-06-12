package ua.tonkoshkur.weather.api.openweather;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.api.openweather.dto.geo.GeoResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.MainDto;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;

@RequiredArgsConstructor
public class WeatherMapper {

    private final String iconUrlFormat;

    public WeatherDto map(WeatherResponse weatherResponse, GeoResponse geoResponse) {
        ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherDto weather = weatherResponse.getWeather().getFirst();
        MainDto main = weatherResponse.getMain();

        return WeatherDto.builder()
                .city(geoResponse.getName())
                .state(geoResponse.getState())
                .countryCode(geoResponse.getCountry())
                .latitude(geoResponse.getLat())
                .longitude(geoResponse.getLon())
                .iconUrl(String.format(iconUrlFormat, weather.getIcon()))
                .name(weather.getMain())
                .description(weather.getDescription())
                .temperature(main.getTemp().intValue())
                .feelsLike(main.getFeelsLike().intValue())
                .minTemperature(main.getTempMin())
                .maxTemperature(main.getTempMax())
                .humidity(main.getHumidity())
                .windSpeed(weatherResponse.getWind().getSpeed())
                .visibility(convertMetersToKilometers(weatherResponse.getVisibilityMeters()))
                .build();
    }

    private int convertMetersToKilometers(int meters) {
        return meters / 1000;
    }
}
