package ua.tonkoshkur.weather.api.openweather;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherHttpClient;
import ua.tonkoshkur.weather.api.WeatherJsonMapper;
import ua.tonkoshkur.weather.api.openweather.dto.geo.GeoResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;
import ua.tonkoshkur.weather.common.util.UrlBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OpenWeatherApi {

    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String ICON_URL_FORMAT = "https://openweathermap.org/img/wn/%s@2x.png";
    // https://openweathermap.org/current
    private static final String WEATHER_DATA_URL = BASE_URL + "/data/2.5/weather";
    // https://openweathermap.org/api/geocoding-api
    private static final String DIRECT_GEO_URL = BASE_URL + "/geo/1.0/direct";
    private static final String REVERSE_GEO_URL = BASE_URL + "/geo/1.0/reverse";

    private static final String APPID_PARAM = "appid";
    private static final String QUERY_PARAM = "q";
    private static final String UNITS_PARAM = "units";
    private static final String DEFAULT_MEASUREMENT_UNIT = "metric";
    private static final String LIMIT_PARAM = "limit";
    private static final int MAX_LOCATIONS_LIMIT = 5;
    private static final String LATITUDE_PARAM = "lat";
    private static final String LONGITUDE_PARAM = "lon";

    private final WeatherJsonMapper jsonMapper = new WeatherJsonMapper();
    private final WeatherHttpClient httpClient;
    private final String apiKey;

    public String getIconUrlFormat() {
        return ICON_URL_FORMAT;
    }

    public List<GeoResponse> findGeoByCity(String city) throws WeatherApiException {
        String url = new UrlBuilder(DIRECT_GEO_URL)
                .addParam(QUERY_PARAM, city)
                .addParam(LIMIT_PARAM, MAX_LOCATIONS_LIMIT)
                .addParam(APPID_PARAM, apiKey)
                .build();
        String json = httpClient.sendRequest(url);
        return jsonMapper.mapList(json, GeoResponse.class);
    }

    public WeatherResponse findWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude) throws WeatherApiException {
        String url = new UrlBuilder(WEATHER_DATA_URL)
                .addParam(LATITUDE_PARAM, latitude)
                .addParam(LONGITUDE_PARAM, longitude)
                .addParam(UNITS_PARAM, DEFAULT_MEASUREMENT_UNIT)
                .addParam(APPID_PARAM, apiKey)
                .build();
        String json = httpClient.sendRequest(url);
        return jsonMapper.map(json, WeatherResponse.class);
    }

    public List<GeoResponse> findGeoByCoordinates(BigDecimal latitude, BigDecimal longitude) throws WeatherApiException {
        String url = new UrlBuilder(REVERSE_GEO_URL)
                .addParam(LATITUDE_PARAM, latitude)
                .addParam(LONGITUDE_PARAM, longitude)
                .addParam(LIMIT_PARAM, MAX_LOCATIONS_LIMIT)
                .addParam(APPID_PARAM, apiKey)
                .build();
        String json = httpClient.sendRequest(url);
        return jsonMapper.mapList(json, GeoResponse.class);
    }
}