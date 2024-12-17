package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stats toStats(EndpointHitDto dto) {
        return Stats.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), formatter))
                .build();
    }
}
