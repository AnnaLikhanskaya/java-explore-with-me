package ru.practicum.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilation.model.CompilationsEvents;

import java.util.List;

public interface CompilationsEventsRepository extends JpaRepository<CompilationsEvents, Long> {

    @Query("SELECT ce " +
            "FROM CompilationsEvents ce " +
            "WHERE ce.compilationId = :compId")
    List<CompilationsEvents> findAllCompilationsEventsByCompilationId(@Param("compId") Long compId);

    @Modifying
    @Query("DELETE FROM CompilationsEvents ce " +
            "WHERE ce.eventId IN :eventsIds")
    void deleteAllByEventsIds(@Param("eventsIds") List<Long> eventsIds);

}