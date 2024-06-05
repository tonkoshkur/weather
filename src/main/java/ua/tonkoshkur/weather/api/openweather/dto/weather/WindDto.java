package ua.tonkoshkur.weather.api.openweather.dto.weather;

import lombok.Data;

@Data
public class WindDto {

    private Double speed;

    private Long deg;

    private Double gust;
}
