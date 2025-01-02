package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.LocationDto;

public interface LocationRepository extends JpaRepository<LocationDto, Long> {

}