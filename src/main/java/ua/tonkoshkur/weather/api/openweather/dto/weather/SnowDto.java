package ua.tonkoshkur.weather.api.openweather.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SnowDto {

    @JsonProperty(value = "1h")
    private Double oneHour;

    @JsonProperty(value = "3h")
    private Double threeHour;
}
