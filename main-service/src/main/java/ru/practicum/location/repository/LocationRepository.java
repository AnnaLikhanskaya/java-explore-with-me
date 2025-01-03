package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.Locations;

public interface LocationRepository extends JpaRepository<Locations, Long> {

}