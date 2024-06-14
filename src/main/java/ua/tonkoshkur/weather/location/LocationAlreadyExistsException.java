package ua.tonkoshkur.weather.location;

public class LocationAlreadyExistsException extends RuntimeException {
    public LocationAlreadyExistsException() {
        super("Location already exists");
    }
}
