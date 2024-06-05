package ua.tonkoshkur.weather.api.openweather.dto.weather;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WeatherResponse {

    @JsonProperty("coord")
    private CoordinatesDto coordinates;

    private List<WeatherDto> weather;

    private String base;

    private MainDto main;

    @JsonProperty("visibility")
    private Integer visibilityMeters;

    private WindDto wind;

    private CloudsDto clouds;

    private RainDto rain;

    private SnowDto snow;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date dt;

    private SysDto sys;

    private Integer timezone;

    private Long id;

    private String name;

    private Integer cod;
}
