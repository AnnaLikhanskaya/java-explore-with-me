package ru.practicum.location.mapper;


import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Locations;

public class LocationMapper {

    public static LocationDto fromLocationDtoToLocation(Locations locations) {
        return LocationDto.builder()
                .lat(locations.getLat())
                .lon(locations.getLon())
                .build();
    }

    public static Locations fromLocationToLocationDto(LocationDto location) {
        return Locations.builder()
                .id(null)
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
