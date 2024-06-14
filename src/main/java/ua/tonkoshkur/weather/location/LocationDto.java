package ua.tonkoshkur.weather.location;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationDto {

    private Integer id;

    private String name;

    private Integer userId;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
