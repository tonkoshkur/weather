package ua.tonkoshkur.weather.api.openweather.dto.geo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LocalNamesDto {

    private String uk;

    private String ru;

    @JsonProperty("feature_name")
    private String featureName;

    private String ascii;
}
