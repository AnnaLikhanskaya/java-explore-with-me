package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, " +
            "new ru.practicum.category.model.CategoryDto(c.id, c.name), e.confirmedRequests, " +
            "e.eventDate, new ru.practicum.user.dto.UserShortDto(e.initiator.id, e.initiator.name), " +
            "e.paid, e.title, e.views) " +
            "FROM Event e JOIN e.category c " +
            "WHERE c.id = :categoryId")
    List<EventShortDto> findFirstEventByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, " +
            "new ru.practicum.category.model.CategoryDto(e.category.id, e.category.name), e.confirmedRequests, " +
            "e.eventDate, new ru.practicum.user.dto.UserShortDto(e.initiator.id, e.initiator.name), " +
            "e.paid, e.title, e.views) " +
            "FROM Event e " +
            "WHERE e.initiator.id = :initiatorId " +
            "ORDER BY e.id ASC")
    List<EventShortDto> findAllEventsByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, " +
            "new ru.practicum.category.model.CategoryDto(e.category.id, e.category.name), e.confirmedRequests, " +
            "e.eventDate, new ru.practicum.user.dto.UserShortDto(e.initiator.id, e.initiator.name), " +
            "e.paid, e.title, e.views) " +
            "FROM Event e " +
            "WHERE e.id IN :eventIds")
    List<EventShortDto> findAllEventsByIds(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state = :state " +
            "AND LOWER(e.annotation) like LOWER(:text) " +
            "AND e.category.id IN :categoriesIds " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "ORDER BY :sort ASC")
    List<Event> findAllEventsByFilterWithoutTime(@Param("text") String textA,
                                                 @Param("categoriesIds") List<Long> categoriesIds,
                                                 @Param("paid") Boolean isPaid,
                                                 Pageable pageable,
                                                 @Param("state") String state,
                                                 @Param("sort") String sort);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state = :state " +
            "AND LOWER(e.annotation) like LOWER(:text) " +
            "AND e.category.id IN :categoriesIds " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate BETWEEN :startDate AND :endDate " +
            "ORDER BY :sort ASC")
    List<Event> findAllEventsByFilter(@Param("text") String text,
                                      @Param("categoriesIds") List<Long> categoriesIds,
                                      @Param("paid") Boolean isPaid,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable,
                                      @Param("state") String state,
                                      @Param("sort") String sort);

    @Query("SELECT e " +
            "FROM Event e " +
            "JOIN CompilationsEvents ce ON ce.eventId = e.id " +
            "WHERE ce.compilationId = :compId")
    List<Event> findEventsByCompilationId(@Param("compId") Long compId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id = :eventId")
    Optional<Event> findEventByEventId(@Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiator.id IN :users) AND " +
            "(:states IS NULL OR e.state IN :states) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(:rangeStart IS NULL OR e.eventDate >= :rangeStart) AND " +
            "(:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> findEventsByCriteria(@Param("users") List<Long> users,
                                     @Param("states") List<String> states,
                                     @Param("categories") List<Long> categories,
                                     @Param("rangeStart") LocalDateTime rangeStart,
                                     @Param("rangeEnd") LocalDateTime rangeEnd,
                                     Pageable pageable);
}