package ua.tonkoshkur.weather.location.weather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.location.Location;
import ua.tonkoshkur.weather.location.LocationDao;
import ua.tonkoshkur.weather.location.LocationDto;
import ua.tonkoshkur.weather.location.LocationMapper;
import ua.tonkoshkur.weather.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationWeatherRepositoryTest {

    private static final String CITY = "London";
    private static final int USER_ID = 10;
    private static WeatherDto weatherDto;
    private static Location location;

    @InjectMocks
    private LocationWeatherRepository locationWeatherRepository;
    @Mock
    private LocationDao locationDao;
    @Mock
    private WeatherApiClient weatherApiClient;
    @Spy
    private final LocationMapper locationMapper = new LocationMapper(new ModelMapper());

    @BeforeAll
    static void setUp() {
        BigDecimal latitude = BigDecimal.valueOf(42.0);
        BigDecimal longitude = BigDecimal.valueOf(52.0);
        weatherDto = WeatherDto.builder()
                .city(CITY)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        User user = new User();
        user.setId(USER_ID);
        location = new Location(CITY, user, latitude, longitude);
        location.setId(1);
    }

    @Test
    void findAllByCity_withApiReturnsWeatherDto_returnsLocationWeatherWithApiWeatherAndNullLocation() {
        when(weatherApiClient.findAllByCity(anyString())).thenReturn(List.of(weatherDto));

        List<LocationWeatherDto> locationWeather = locationWeatherRepository.findAllByCity(CITY);

        assertThat(locationWeather)
                .singleElement()
                .satisfies(lw -> {
                    assertThat(lw.weather()).isEqualTo(weatherDto);
                    assertThat(lw.location()).isNull();
                });
    }

    @Test
    void findAllByUserId_withApiReturnsWeatherDto_returnsLocationWeatherWithApiWeatherAndDaoLocation() {
        when(locationDao.findAllByUserId(anyInt())).thenReturn(List.of(location));
        when(weatherApiClient.findByCoordinates(any(), any())).thenReturn(Optional.of(weatherDto));

        List<LocationWeatherDto> locationWeather = locationWeatherRepository.findAllByUserId(USER_ID);

        assertThat(locationWeather)
                .singleElement()
                .satisfies(lw -> {
                    assertEquals(weatherDto, lw.weather());
                    assertThat(lw.location())
                            .satisfies(this::assertThatLocationDtoFieldsEqualsLocation);
                });
    }

    @Test
    void findAllByCityAndUserId_withApiReturnsWeatherDto_returnsLocationWeatherWithApiWeatherAndDaoLocation() {
        when(locationDao.findAllByUserId(anyInt())).thenReturn(List.of(location));
        when(weatherApiClient.findAllByCity(anyString())).thenReturn(List.of(weatherDto));

        List<LocationWeatherDto> locationWeather = locationWeatherRepository.findAllByCityAndUserId(CITY, USER_ID);

        assertThat(locationWeather)
                .singleElement()
                .satisfies(lw -> {
                    assertEquals(weatherDto, lw.weather());
                    assertThat(lw.location())
                            .satisfies(this::assertThatLocationDtoFieldsEqualsLocation);
                });
    }

    private void assertThatLocationDtoFieldsEqualsLocation(LocationDto locationDto) {
        assertEquals(location.getName(), locationDto.getName());
        assertEquals(location.getUser().getId(), locationDto.getUserId());
        assertEquals(location.getLatitude(), locationDto.getLatitude());
        assertEquals(location.getLongitude(), locationDto.getLongitude());
    }
}
