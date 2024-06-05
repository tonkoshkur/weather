package ua.tonkoshkur.weather.api.openweather.dto.geo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GeoResponse {

    private String name;

    @JsonProperty("local_names")
    private LocalNamesDto localNames;

    private BigDecimal lat;

    private BigDecimal lon;

    private String country;

    private String state;
}
