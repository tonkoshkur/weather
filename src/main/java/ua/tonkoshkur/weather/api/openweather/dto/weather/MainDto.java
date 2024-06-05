package ua.tonkoshkur.weather.api.openweather.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MainDto {

    private Double temp;

    @JsonProperty("feels_like")
    private Double feelsLike;

    private Integer pressure;

    private Integer humidity;

    @JsonProperty("temp_min")
    private Double tempMin;

    @JsonProperty("temp_max")
    private Double tempMax;

    @JsonProperty("sea_level")
    private Double seaLevel;

    @JsonProperty("grnd_level")
    private Double groundLevel;
}
