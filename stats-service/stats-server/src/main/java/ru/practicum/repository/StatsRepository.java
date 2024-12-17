package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT new ru.practicum.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Stats e " +
            "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC ")
    List<ViewStatsDto> findAllStatsWithUniqueAndUris(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end,
                                                     @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Stats e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC ")
    List<ViewStatsDto> findAllStatsWithUnique(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Stats e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<ViewStatsDto> findAllStats(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Stats e " +
            "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<ViewStatsDto> findAllStatsWithUris(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);
}
