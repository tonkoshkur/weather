package ua.tonkoshkur.weather.location.weather;

import ua.tonkoshkur.weather.api.WeatherDto;

public record LocationWeatherDto(Integer locationId, String name, WeatherDto weather) {
    public LocationWeatherDto(WeatherDto weather) {
        this(null, null, weather);
    }
}
