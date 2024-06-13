package ua.tonkoshkur.weather.api.openweather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.api.openweather.dto.geo.GeoResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.MainDto;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherResponse;
import ua.tonkoshkur.weather.api.openweather.dto.weather.WindDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiClientTest {

    private static OpenWeatherApiClient openWeatherApiClient;
    private static OpenWeatherApi openWeatherApi;
    private static GeoResponse geoResponse = new GeoResponse();
    private static WeatherResponse weatherResponse = new WeatherResponse();
    private static WeatherDto expectedWeather = WeatherDto.builder().build();

    @BeforeAll
    static void setUp() {
        openWeatherApi = spy(new OpenWeatherApi(null, ""));
        openWeatherApiClient = new OpenWeatherApiClient(openWeatherApi);
        setUpGeoResponse();
        setUpWeatherResponse();
        setUpExpectedWeatherDto();
    }

    private static void setUpGeoResponse() {
        geoResponse = new GeoResponse();
        geoResponse.setName("London");
        geoResponse.setLat(new BigDecimal("89.999"));
        geoResponse.setLon(new BigDecimal("179.999"));
        geoResponse.setState("England");
        geoResponse.setCountry("GB");
    }

    private static void setUpWeatherResponse() {
        weatherResponse = new WeatherResponse();
        weatherResponse.setVisibilityMeters(10000);

        MainDto main = new MainDto();
        main.setTemp(11.22);
        main.setFeelsLike(22.33);
        main.setTempMin(33.44);
        main.setTempMax(44.55);
        main.setHumidity(10);
        weatherResponse.setMain(main);

        ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherDto weather
                = new ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherDto();
        weather.setMain("Clouds");
        weather.setDescription("few clouds");
        weather.setIcon("02d");
        weatherResponse.setWeather(List.of(weather));

        WindDto wind = new WindDto();
        wind.setSpeed(55.66);
        weatherResponse.setWind(wind);
    }

    private static void setUpExpectedWeatherDto() {
        ua.tonkoshkur.weather.api.openweather.dto.weather.WeatherDto weatherResponseWeather
                = weatherResponse.getWeather().getFirst();
        MainDto main = weatherResponse.getMain();
        expectedWeather = WeatherDto.builder()
                .city(geoResponse.getName())
                .state(geoResponse.getState())
                .countryCode(geoResponse.getCountry())
                .latitude(geoResponse.getLat())
                .longitude(geoResponse.getLon())
                .name(weatherResponseWeather.getMain())
                .description(weatherResponseWeather.getDescription())
                .iconUrl(String.format(openWeatherApi.getIconUrlFormat(), weatherResponseWeather.getIcon()))
                .temperature(main.getTemp().intValue())
                .feelsLike(main.getFeelsLike().intValue())
                .minTemperature(main.getTempMin())
                .maxTemperature(main.getTempMax())
                .humidity(main.getHumidity())
                .windSpeed(weatherResponse.getWind().getSpeed())
                .visibility(weatherResponse.getVisibilityMeters() / 1000)
                .build();
    }

    @Test
    void findAllByCity_withGeoResponseCity_returnsCorrectWeatherDto() {
        doReturn(List.of(geoResponse)).when(openWeatherApi).findGeoByCity(anyString());
        doReturn(weatherResponse).when(openWeatherApi).findWeatherByCoordinates(any(), any());

        List<WeatherDto> weather = openWeatherApiClient.findAllByCity(geoResponse.getName());

        assertThat(weather)
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(expectedWeather);
    }

    @Test
    void findAllByCoordinates_withGeoResponseCoordinates_returnsCorrectWeatherDto() {
        doReturn(Optional.of(geoResponse)).when(openWeatherApi).findGeoByCoordinates(any(), any());
        doReturn(weatherResponse).when(openWeatherApi).findWeatherByCoordinates(any(), any());

        Optional<WeatherDto> weather = openWeatherApiClient.findByCoordinates(geoResponse.getLat(), geoResponse.getLon());

        assertThat(weather)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedWeather);
    }
}
