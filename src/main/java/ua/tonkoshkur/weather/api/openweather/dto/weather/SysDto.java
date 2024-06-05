package ua.tonkoshkur.weather.api.openweather.dto.weather;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SysDto {

    private Long id;

    private Long type;

    private String message;

    private String country;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date sunrise;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date sunset;
}
