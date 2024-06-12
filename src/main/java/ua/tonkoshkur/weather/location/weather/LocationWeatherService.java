package ua.tonkoshkur.weather.location.weather;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.user.User;

import java.util.List;

@RequiredArgsConstructor
public class LocationWeatherService {

    private final LocationWeatherRepository locationWeatherRepository;

    public List<LocationWeatherDto> findAllByCityAndUser(String city, User user) {
        if (isSearchValid(city)) {
            return user != null
                    ? locationWeatherRepository.findAllByCityAndUserId(city, user.getId())
                    : locationWeatherRepository.findAllByCity(city);
        }
        if (user != null) {
            return locationWeatherRepository.findAllByUserId(user.getId());
        }
        return List.of();
    }

    private boolean isSearchValid(String search) {
        return search != null && !search.isBlank();
    }
}
