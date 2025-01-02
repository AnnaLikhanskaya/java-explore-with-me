package ru.practicum.location.mapper;


import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationDto;

public class LocationMapper {

    public static Location fromLocationDtoToLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto fromLocationToLocationDto(Location location) {
        return LocationDto.builder()
                .id(null)
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
