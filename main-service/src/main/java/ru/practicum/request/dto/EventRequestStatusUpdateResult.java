package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequest> confirmedRequests;

    private List<ParticipationRequest> rejectedRequests;
}