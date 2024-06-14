package ua.tonkoshkur.weather.location;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class LocationMapper {

    private final ModelMapper modelMapper;

    public LocationDto toDto(Location entity) {
        return modelMapper.map(entity, LocationDto.class);
    }
}
