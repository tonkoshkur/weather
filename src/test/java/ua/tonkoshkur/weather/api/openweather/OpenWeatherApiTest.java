package ua.tonkoshkur.weather.api.openweather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherHttpClient;
import ua.tonkoshkur.weather.api.openweather.dto.geo.GeoResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiTest {

    private static final String CITY = "London";
    private static final BigDecimal LATITUDE = BigDecimal.valueOf(51.5073219);
    private static final BigDecimal LONGITUDE = BigDecimal.valueOf(-0.1276474);
    private static final String GEO_RESPONSE = """
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
    private static final String WEATHER_RESPONSE = """
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

    private static OpenWeatherApi openWeatherApi;
    private static WeatherHttpClient httpClient;

    @BeforeAll
    static void setUp() {
        httpClient = Mockito.mock(WeatherHttpClient.class);
        openWeatherApi = new OpenWeatherApi(httpClient, "");
    }

    @Test
    void findGeoByCity_whenHttpClientReturnsValidResponse_returnsCorrectGeoResponse() {
        when(httpClient.sendRequest(anyString())).thenReturn(GEO_RESPONSE);

        List<GeoResponse> geoResponses = openWeatherApi.findGeoByCity(CITY);

        assertThat(geoResponses)
                .anySatisfy(this::assertThatCorrectGeoResponse);
    }

    @Test
    void findGeoByCoordinates_whenHttpClientReturnsValidResponse_returnsCorrectGeoResponse() {
        when(httpClient.sendRequest(anyString())).thenReturn(GEO_RESPONSE);

        List<GeoResponse> optionalResponse = openWeatherApi.findGeoByCoordinates(LATITUDE, LONGITUDE);

        assertThat(optionalResponse)
                .singleElement()
                .satisfies(this::assertThatCorrectGeoResponse);
    }

    private void assertThatCorrectGeoResponse(GeoResponse geoResponse) {
        assertEquals(CITY, geoResponse.getName());
        assertEquals("England", geoResponse.getState());
        assertEquals("GB", geoResponse.getCountry());
        assertThat(geoResponse.getLat()).isEqualTo(LATITUDE);
        assertThat(geoResponse.getLon()).isEqualTo(LONGITUDE);
    }

    @Test
    void findWeatherByCoordinates_whenHttpClientReturnsValidResponse_returnsCorrectWeatherResponse() {
        BigDecimal latitude = new BigDecimal("51.5085");
        BigDecimal longitude = new BigDecimal("-0.1257");
        when(httpClient.sendRequest(anyString())).thenReturn(WEATHER_RESPONSE);

        WeatherResponse weatherResponse = openWeatherApi.findWeatherByCoordinates(latitude, longitude);

        assertEquals(CITY, weatherResponse.getName());
        assertThat(weatherResponse.getCoordinates())
                .satisfies(c -> {
                    assertEquals(latitude, c.getLat());
                    assertEquals(longitude, c.getLon());
                });
        assertThat(weatherResponse.getMain())
                .hasNoNullFieldsOrPropertiesExcept("seaLevel", "groundLevel");
        assertThat(weatherResponse.getWeather())
                .isNotEmpty()
                .first()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void findGeoByCity_whenHttpClientReturnsInvalidResponse_throwsWeatherApiException() {
        when(httpClient.sendRequest(anyString())).thenReturn(anyString());
        assertThrows(WeatherApiException.class, () -> openWeatherApi.findGeoByCity(CITY));
    }

    @Test
    void findGeoByCoordinates_whenHttpClientReturnsInvalidResponse_throwsWeatherApiException() {
        when(httpClient.sendRequest(anyString())).thenReturn(anyString());
        assertThrows(WeatherApiException.class, () -> openWeatherApi.findGeoByCoordinates(LATITUDE, LONGITUDE));
    }

    @Test
    void findWeatherByCoordinates_whenHttpClientReturnsInvalidResponse_throwsWeatherApiException() {
        when(httpClient.sendRequest(anyString())).thenReturn(anyString());
        assertThrows(WeatherApiException.class, () -> openWeatherApi.findWeatherByCoordinates(LATITUDE, LONGITUDE));
    }
}
