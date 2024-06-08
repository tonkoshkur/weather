package ua.tonkoshkur.weather.api.openweather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.api.WeatherHttpClient;
import ua.tonkoshkur.weather.api.openweather.dto.geo.GeoResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.CoordinatesDto;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiTest {

    private static final String CITY = "London";
    private static final String FIND_GEO_BY_CITY_RESPONSE = """
            [
                {
                    "name":"London",
                    "local_names": {
                        "en":"London",
                        "uk":"Лондон",
                        "ru":"Лондон"
                    },
                    "lat":51.5073219,
                    "lon":-0.1276474,
                    "country":"GB",
                    "state":"England"
                }
            ]
            """;
    private static final String FIND_BY_CITY_RESPONSE = """
            {
                "coord": {
                    "lon": -0.1257,
                    "lat": 51.5085
                },
                "weather": [
                    {
                        "id": 801,
                        "main": "Clouds",
                        "description": "few clouds",
                        "icon": "02d"
                    }
                ],
                "base": "stations",
                "main": {
                    "temp": 18.16,
                    "feels_like": 17.34,
                    "temp_min": 16.11,
                    "temp_max": 19.72,
                    "pressure": 1017,
                    "humidity": 50
                },
                "visibility": 10000,
                "wind": {
                    "speed": 3.09,
                    "deg": 250
                },
                "clouds": {
                    "all": 20
                },
                "dt": 1717682152,
                "sys": {
                    "type": 2,
                    "id": 2075535,
                    "country": "GB",
                    "sunrise": 1717645535,
                    "sunset": 1717704791
                },
                "timezone": 3600,
                "id": 2643743,
                "name": "London",
                "cod": 200
            }
            """;

    private static WeatherHttpClient httpClient;
    private static OpenWeatherApi openWeatherApi;

    @BeforeAll
    static void setUp() {
        httpClient = Mockito.mock(WeatherHttpClient.class);
        openWeatherApi = new OpenWeatherApi(httpClient, "");
    }

    @Test
    void givenCityName_whenFindGeoByCity_thenReturnGeoResponseWithTheCity()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(httpClient.sendRequest(anyString())).thenReturn(FIND_GEO_BY_CITY_RESPONSE);

        Method findGeoByCityMethod = OpenWeatherApi.class.getDeclaredMethod("findGeoByCity", String.class);
        findGeoByCityMethod.setAccessible(true);
        List<GeoResponse> geoResponses = (List<GeoResponse>) findGeoByCityMethod.invoke(openWeatherApi, CITY);

        assertThat(geoResponses)
                .anyMatch(response -> response.getName().equals(CITY)
                        && response.getCountry().equals("GB")
                        && response.getState().equals("England")
                        && response.getLat().equals(new BigDecimal("51.5073219"))
                        && response.getLon().equals(new BigDecimal("-0.1276474")));
    }

    @Test
    void givenCityName_whenFindByCity_thenReturnWeatherResponseWithTheCity()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(httpClient.sendRequest(anyString())).thenReturn(FIND_BY_CITY_RESPONSE);

        Method findByCityMethod = OpenWeatherApi.class.getDeclaredMethod("findByCity", String.class);
        findByCityMethod.setAccessible(true);
        WeatherResponse weatherResponse = (WeatherResponse) findByCityMethod.invoke(openWeatherApi, CITY);

        assertThat(weatherResponse.getName()).isEqualTo(CITY);
        assertThat(weatherResponse.getSys().getCountry()).isEqualTo("GB");
        assertThat(weatherResponse.getMain()).hasNoNullFieldsOrPropertiesExcept("seaLevel", "groundLevel");
        CoordinatesDto coordinates = weatherResponse.getCoordinates();
        assertThat(coordinates.getLat()).isEqualTo("51.5085");
        assertThat(coordinates.getLon()).isEqualTo("-0.1257");
    }

    @Test
    void givenCityName_whenFindAllByCity_thenReturnCityWeather() {
        when(httpClient.sendRequest(anyString())).thenReturn(FIND_GEO_BY_CITY_RESPONSE, FIND_BY_CITY_RESPONSE);

        List<WeatherDto> weather = openWeatherApi.findAllByCity(CITY);

        assertThat(weather)
                .singleElement()
                .hasNoNullFieldsOrProperties()
                .satisfies(w -> {
                    assertThat(w.getCity()).isEqualTo(CITY);
                    assertThat(w.getCountryCode()).isEqualTo("GB");
                    assertThat(w.getState()).isEqualTo("England");
                    assertThat(w.getLatitude()).isEqualTo("51.5085");
                    assertThat(w.getLongitude()).isEqualTo("-0.1257");
                });
    }

    @Test
    void givenNotValidResponse_whenFindAllByCity_thenThrowWeatherApiException() {
        assertThrows(WeatherApiException.class, () -> openWeatherApi.findAllByCity(CITY));
    }
}
