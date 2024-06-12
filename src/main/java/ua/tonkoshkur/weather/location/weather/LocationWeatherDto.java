package ua.tonkoshkur.weather.location.weather;

import ua.tonkoshkur.weather.api.WeatherDto;

public record LocationWeatherDto(Integer locationId, WeatherDto weather) {
    public LocationWeatherDto(WeatherDto weather) {
        this(null, weather);
    }
}
