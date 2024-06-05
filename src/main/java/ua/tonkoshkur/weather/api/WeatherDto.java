package ua.tonkoshkur.weather.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@Builder
public class WeatherDto {
    private String city;
    private String state;
    private String countryCode;
    private String iconUrl;
    private String name;
    private String description;
    private Integer temperature;
    private Integer feelsLike;
    private Double minTemperature;
    private Double maxTemperature;
    private Integer humidity;
    private Double windSpeed;
    private Integer visibility;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
