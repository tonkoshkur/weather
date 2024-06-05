package ua.tonkoshkur.weather.api.openweather.dto.weather;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoordinatesDto {

    private BigDecimal lat;

    private BigDecimal lon;
}
