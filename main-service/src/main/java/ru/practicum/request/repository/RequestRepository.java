package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT r " +
            "FROM ParticipationRequest r " +
            "WHERE r.event = :eventId ")
    List<ParticipationRequest> findAllRequestsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r " +
            "FROM ParticipationRequest r " +
            "WHERE r.requester = :requesterId")
    List<ParticipationRequest> findAllByRequesterId(@Param("requesterId") Long requesterId);

    @Query("SELECT r " +
            "FROM ParticipationRequest r " +
            "WHERE r.requester = :requesterId AND r.event = :eventId")
    Optional<ParticipationRequest> findByRequesterIdAndEventId(@Param("requesterId") Long requesterId,
                                                               @Param("eventId") Long eventId);

}