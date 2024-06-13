package ua.tonkoshkur.weather.location.weather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tonkoshkur.weather.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationWeatherServiceTest {

    private static String city;
    private static User user;

    @InjectMocks
    private LocationWeatherService service;
    @Mock
    private LocationWeatherRepository repository;

    @BeforeAll
    static void setUp() {
        city = "London";
        user = new User();
        user.setId(1);
    }

    @Test
    void findAllByCityAndUser_withNullArgs_returnEmptyList() {
        assertThat(service.findAllByCityAndUser(null, null)).isEmpty();

        verify(repository, never()).findAllByCity(anyString());
        verify(repository, never()).findAllByUserId(anyInt());
        verify(repository, never()).findAllByCityAndUserId(anyString(), anyInt());
    }

    @Test
    void findAllByCityAndUser_withSomeCityAndNullUser_verifyOnlyRepoFindAllByCityMethod() {
        service.findAllByCityAndUser(city, null);

        verify(repository, only()).findAllByCity(anyString());
        verify(repository, never()).findAllByUserId(anyInt());
        verify(repository, never()).findAllByCityAndUserId(anyString(), anyInt());
    }

    @Test
    void findAllByCityAndUser_withNullCityAndSomeUser_verifyOnlyRepoFindAllByUserIdMethod() {
        service.findAllByCityAndUser(null, user);

        verify(repository, never()).findAllByCity(anyString());
        verify(repository, only()).findAllByUserId(anyInt());
        verify(repository, never()).findAllByCityAndUserId(anyString(), anyInt());
    }

    @Test
    void findAllByCityAndUser_withSomeCityAndSomeUser_verifyOnlyRepoFindAllByCityAndUserIdMethod() {
        service.findAllByCityAndUser(city, user);

        verify(repository, never()).findAllByCity(anyString());
        verify(repository, never()).findAllByUserId(anyInt());
        verify(repository, only()).findAllByCityAndUserId(anyString(), anyInt());
    }
}
