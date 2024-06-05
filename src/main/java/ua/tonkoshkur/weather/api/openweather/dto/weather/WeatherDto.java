package ua.tonkoshkur.weather.api.openweather.dto.weather;

import lombok.Data;

@Data
public class WeatherDto {

    private Long id;

    private String main;

    private String description;

    private String icon;
}
