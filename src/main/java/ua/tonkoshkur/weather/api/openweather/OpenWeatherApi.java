package ua.tonkoshkur.weather.api.openweather;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.api.*;
import ua.tonkoshkur.weather.api.openweather.dto.geo.GeoResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;
import ua.tonkoshkur.weather.common.util.UrlBuilder;

import java.util.List;

@RequiredArgsConstructor
public class OpenWeatherApi implements WeatherApi {

    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String ICON_URL_FORMAT = "https://openweathermap.org/img/wn/%s@2x.png";
    // https://openweathermap.org/current
    private static final String WEATHER_DATA_URL = BASE_URL + "/data/2.5/weather";
    // https://openweathermap.org/api/geocoding-api
    private static final String DIRECT_GEO_URL = BASE_URL + "/geo/1.0/direct";

    private static final String APPID_PARAM = "appid";
    private static final String QUERY_PARAM = "q";
    private static final String UNITS_PARAM = "units";
    private static final String DEFAULT_MEASUREMENT_UNIT = "metric";
    private static final String LIMIT_PARAM = "limit";
    private static final int MAX_LOCATIONS_LIMIT = 5;

    private final WeatherHttpClient httpClient = new WeatherHttpClient();
    private final WeatherMapper weatherMapper = new WeatherMapper(ICON_URL_FORMAT);
    private final WeatherJsonMapper jsonMapper = new WeatherJsonMapper();
    private final String apiKey;

    public List<WeatherDto> findAllByCity(String city) throws WeatherApiException {
        return findGeoByCity(city).stream()
                .map(geoResponse -> weatherMapper.map(findByCity(geoResponse.getName()), geoResponse))
                .distinct()
                .toList();
    }

    private List<GeoResponse> findGeoByCity(String city) throws WeatherApiException {
        String url = new UrlBuilder(DIRECT_GEO_URL)
                .addParam(QUERY_PARAM, city)
                .addParam(LIMIT_PARAM, MAX_LOCATIONS_LIMIT)
                .addParam(APPID_PARAM, apiKey)
                .build();
        String json = httpClient.sendRequest(url);
        return jsonMapper.mapList(json, GeoResponse.class);
    }

    private WeatherResponse findByCity(String city) throws WeatherApiException {
        String url = new UrlBuilder(WEATHER_DATA_URL)
                .addParam(QUERY_PARAM, city)
                .addParam(UNITS_PARAM, DEFAULT_MEASUREMENT_UNIT)
                .addParam(APPID_PARAM, apiKey)
                .build();
        String json = httpClient.sendRequest(url);
        return jsonMapper.map(json, WeatherResponse.class);
    }
}