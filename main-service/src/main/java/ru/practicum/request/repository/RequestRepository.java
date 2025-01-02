package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.ParticipationRequestDto;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequestDto, Long> {

    @Query("SELECT r " +
            "FROM ParticipationRequestDto r " +
            "WHERE r.event = :eventId ")
    List<ParticipationRequestDto> findAllRequestsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r " +
            "FROM ParticipationRequestDto r " +
            "WHERE r.requester = :requesterId")
    List<ParticipationRequestDto> findAllByRequesterId(@Param("requesterId") Long requesterId);

    @Query("SELECT r " +
            "FROM ParticipationRequestDto r " +
            "WHERE r.requester = :requesterId AND r.event = :eventId")
    Optional<ParticipationRequestDto> findByRequesterIdAndEventId(@Param("requesterId") Long requesterId,
                                                                  @Param("eventId") Long eventId);

}