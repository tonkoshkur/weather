package ua.tonkoshkur.weather.api.openweather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.api.WeatherHttpClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiClientTest {

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
    private static OpenWeatherApiClient openWeatherApiClient;

    @BeforeAll
    static void setUp() {
        httpClient = Mockito.mock(WeatherHttpClient.class);
        openWeatherApiClient = new OpenWeatherApiClient(httpClient, "");
    }

    @Test
    void givenCityName_whenFindAllByCity_thenReturnCityWeather() {
        when(httpClient.sendRequest(anyString())).thenReturn(FIND_GEO_BY_CITY_RESPONSE, FIND_BY_CITY_RESPONSE);

        List<WeatherDto> weather = openWeatherApiClient.findAllByCity(CITY);

        assertThat(weather)
                .singleElement()
                .hasNoNullFieldsOrProperties()
                .satisfies(w -> {
                    assertThat(w.getCity()).isEqualTo(CITY);
                    assertThat(w.getCountryCode()).isEqualTo("GB");
                    assertThat(w.getState()).isEqualTo("England");
                    assertThat(w.getLatitude()).isEqualTo("51.5073219");
                    assertThat(w.getLongitude()).isEqualTo("-0.1276474");
                });
    }

    @Test
    void givenNotValidResponse_whenFindAllByCity_thenThrowWeatherApiException() {
        assertThrows(WeatherApiException.class, () -> openWeatherApiClient.findAllByCity(CITY));
    }
}
