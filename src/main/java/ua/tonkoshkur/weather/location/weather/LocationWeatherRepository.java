package ua.tonkoshkur.weather.location.weather;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.location.LocationDao;
import ua.tonkoshkur.weather.location.LocationDto;
import ua.tonkoshkur.weather.location.LocationMapper;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LocationWeatherRepository {

    private final LocationDao locationDao;
    private final WeatherApiClient weatherApiClient;
    private final LocationMapper locationMapper;

    public List<LocationWeatherDto> findAllByCity(String city) {
        return weatherApiClient.findAllByCity(city)
                .stream()
                .map(LocationWeatherDto::new)
                .toList();
    }

    public List<LocationWeatherDto> findAllByUserId(int userId) {
        return locationDao.findAllByUserId(userId)
                .stream()
                .map(locationMapper::toDto)
                .flatMap(this::findAllByLocation)
                .toList();
    }

    private Stream<LocationWeatherDto> findAllByLocation(LocationDto location) {
        return weatherApiClient.findByCoordinates(location.getLatitude(), location.getLongitude())
                .stream()
                .map(weather -> new LocationWeatherDto(location, weather));
    }

    public List<LocationWeatherDto> findAllByCityAndUserId(String city, int userId) {
        List<LocationDto> locations = locationDao.findAllByUserId(userId)
                .stream()
                .map(locationMapper::toDto)
                .toList();
        return weatherApiClient.findAllByCity(city)
                .stream()
                .map(weather -> createLocationWeatherDto(weather, locations))
                .toList();
    }

    private LocationWeatherDto createLocationWeatherDto(WeatherDto weather, List<LocationDto> locations) {
        return locations.stream()
                .filter(location -> isWeatherMatchingLocation(weather, location))
                .findAny()
                .map(location -> new LocationWeatherDto(location, weather))
                .orElse(new LocationWeatherDto(weather));
    }

    private boolean isWeatherMatchingLocation(WeatherDto weather, LocationDto location) {
        return weather.getLatitude().compareTo(location.getLatitude()) == 0
                && weather.getLongitude().compareTo(location.getLongitude()) == 0;
    }
}
