package ua.tonkoshkur.weather.location.weather;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.location.Location;
import ua.tonkoshkur.weather.location.LocationDao;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LocationWeatherRepository {

    private final LocationDao locationDao;
    private final WeatherApiClient weatherApiClient;

    public List<LocationWeatherDto> findAllByCity(String city) {
        return weatherApiClient.findAllByCity(city)
                .stream()
                .map(LocationWeatherDto::new)
                .toList();
    }

    public List<LocationWeatherDto> findAllByUserId(int userId) {
        return locationDao.findAllByUserId(userId)
                .stream()
                .flatMap(this::findAllByLocation)
                .toList();
    }

    private Stream<LocationWeatherDto> findAllByLocation(Location location) {
        return weatherApiClient.findByCoordinates(location.getLatitude(), location.getLongitude())
                .stream()
                .map(weather -> new LocationWeatherDto(location.getId(), weather));
    }

    public List<LocationWeatherDto> findAllByCityAndUserId(String city, int userId) {
        List<Location> locations = locationDao.findAllByUserId(userId);
        return weatherApiClient.findAllByCity(city)
                .stream()
                .map(weather -> createLocationWeatherDto(weather, locations))
                .toList();
    }

    private LocationWeatherDto createLocationWeatherDto(WeatherDto weather, List<Location> locations) {
        return locations.stream()
                .filter(location -> isWeatherMatchingLocation(weather, location))
                .findAny()
                .map(location -> new LocationWeatherDto(location.getId(), weather))
                .orElse(new LocationWeatherDto(weather));
    }

    private boolean isWeatherMatchingLocation(WeatherDto weather, Location location) {
        return location.getLatitude().stripTrailingZeros().equals(weather.getLatitude())
                && location.getLongitude().stripTrailingZeros().equals(weather.getLongitude());
    }
}
