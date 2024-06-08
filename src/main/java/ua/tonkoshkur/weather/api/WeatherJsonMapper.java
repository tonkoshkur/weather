package ua.tonkoshkur.weather.api;

import com.fasterxml.jackson.databind.json.JsonMapper;

import java.util.List;

public class WeatherJsonMapper {

    private final JsonMapper jsonMapper = new JsonMapper();

    public <T> T map(String json, Class<T> type) throws WeatherApiException {
        try {
            return jsonMapper.readValue(json, type);
        } catch (Exception e) {
            throw new WeatherApiException(e);
        }
    }

    public <T> List<T> mapList(String json, Class<T> type) throws WeatherApiException {
        try {
            return jsonMapper.readerForListOf(type)
                    .readValue(json);
        } catch (Exception e) {
            throw new WeatherApiException(e);
        }
    }
}
