package ua.tonkoshkur.weather.location.weather;

import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.location.LocationDto;

public record LocationWeatherDto(LocationDto location, WeatherDto weather) {
    public LocationWeatherDto(WeatherDto weather) {
        this(null, weather);
    }
}
